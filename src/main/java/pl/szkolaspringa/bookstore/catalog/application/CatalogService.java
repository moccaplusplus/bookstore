package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase.SaveUploadCommand;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final AuthorJpaRepository authorJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final UploadUseCase uploadUseCase;

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        return bookJpaRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByAuthor(String author) {
        return bookJpaRepository.findByAuthor(author);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByTitle(String title) {
        return bookJpaRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return bookJpaRepository.findByTitleAndAuthor(title, author);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findOneById(Long id) {
        return bookJpaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findOneByTitle(String title) {
        return bookJpaRepository.findOneByTitle(title);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return bookJpaRepository.findOneByTitleAndAuthor(title, author);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAllWithAuthors(String title, String author) {
        return bookJpaRepository.findAllWithAuthors(title, author);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findOneWithAuthors(Long id) {
        return bookJpaRepository.findOneWithAuthors(id);
    }

    @Transactional
    @Override
    public Book addBook(AddBookCommand command) {
        var book = new Book(command.title(), command.year(), command.price());
        book.setAuthors(command.authors().stream().map(authorJpaRepository::getReferenceById).collect(toSet()));
//        var authors = fetchAuthorsByIds(command.authors());
//        authors.forEach(book::addAuthor);
        return bookJpaRepository.save(book);
    }

    @Transactional
    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return bookJpaRepository.findById(command.id()).map(book -> {
            Optional.of(command).map(UpdateBookCommand::title).ifPresent(book::setTitle);
            Optional.of(command).map(UpdateBookCommand::year).ifPresent(book::setYear);
            Optional.of(command).map(UpdateBookCommand::price).ifPresent(book::setPrice);
            Optional.of(command).map(UpdateBookCommand::authors)
                    .map(authorIds -> authorIds.stream().map(authorJpaRepository::getReferenceById).collect(toSet()))
                    .ifPresent(book::setAuthors);
            return UpdateBookResponse.SUCCESS;
        }).orElseGet(() -> UpdateBookResponse.errorResponse("Book not found"));
    }

    @Transactional
    @Override
    public void removeById(Long id) {
        bookJpaRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        bookJpaRepository.findById(command.id()).ifPresent(book -> {
            var cmd = new SaveUploadCommand(command.fileName(), command.file(), command.contentType());
            var saved = uploadUseCase.save(cmd);
            book.setCoverId(saved.getId());
        });
    }

    @Transactional
    @Override
    public void removeBookCover(Long id) {
        bookJpaRepository.findById(id).ifPresent(book -> Optional.ofNullable(book.getCoverId()).ifPresent(coverId -> {
            uploadUseCase.removeById(coverId);
            book.setCoverId(null);
        }));
    }

    private List<Author> fetchAuthorsByIds(Collection<Long> authorIds) {
        var authors = authorJpaRepository.findAllById(authorIds);
        System.out.println("Authors: " + authors);
        if (authors.size() < authorIds.size()) {
            var missingAuthorIds = authorIds.stream()
                    .filter(not(authors.stream().map(Author::getId).collect(toSet())::contains))
                    .map(String::valueOf)
                    .collect(joining(", "));
            throw new IllegalArgumentException("Unable to find authors with ids: " + missingAuthorIds + ".");
        }
        return authors;
    }
}
