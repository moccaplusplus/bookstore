package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase.SaveUploadCommand;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final AuthorJpaRepository authorJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final UploadUseCase uploadUseCase;

    @Override
    public List<Book> findAll() {
        return bookJpaRepository.findAll();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookJpaRepository.findByAuthor(author);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookJpaRepository.findByTitle(title);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return bookJpaRepository.findByTitleAndAuthor(title, author);
    }

    @Override
    public Optional<Book> findOneById(Long id) {
        return bookJpaRepository.findById(id);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return bookJpaRepository.findOneByTitle(title);
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return bookJpaRepository.findOneByTitleAndAuthor(title, author);
    }

    @Override
    public Book addBook(AddBookCommand command) {
        var book = new Book(command.title(), command.year(), command.price());
        var authors = fetchAuthorsByIds(command.authors());
        book.setAuthors(authors);
        return bookJpaRepository.save(book);
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        var found = bookJpaRepository.findById(command.id());
        if (found.isEmpty()) return UpdateBookResponse.errorResponse("Book not found");
        var book = found.get();
        Optional.of(command).map(UpdateBookCommand::title).ifPresent(book::setTitle);
        Optional.of(command).map(UpdateBookCommand::year).ifPresent(book::setYear);
        Optional.of(command).map(UpdateBookCommand::price).ifPresent(book::setPrice);
        Optional.of(command).map(UpdateBookCommand::authors).filter(not(Collection::isEmpty))
                .map(this::fetchAuthorsByIds).ifPresent(book::setAuthors);
        bookJpaRepository.save(book);
        return UpdateBookResponse.SUCCESS;
    }

    @Override
    public void removeById(Long id) {
        bookJpaRepository.deleteById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        bookJpaRepository.findById(command.id()).ifPresent(book -> {
            var cmd = new SaveUploadCommand(command.fileName(), command.file(), command.contentType());
            var saved = uploadUseCase.save(cmd);
            book.setCoverId(saved.getId());
            bookJpaRepository.save(book);
        });
    }

    @Override
    public void removeBookCover(Long id) {
        bookJpaRepository.findById(id).ifPresent(book -> {
            Optional.ofNullable(book.getCoverId()).ifPresent(coverId -> {
                uploadUseCase.removeById(coverId);
                book.setCoverId(null);
                bookJpaRepository.save(book);
            });

        });
    }

    private Set<Author> fetchAuthorsByIds(Collection<Long> authorIds) {
        var authors = authorJpaRepository.findAllById(authorIds);
        System.out.println("Authors: " + authors);
        if (authors.size() < authorIds.size()) {
            var missingAuthorIds = authorIds.stream()
                    .filter(not(authors.stream().map(Author::getId).collect(toSet())::contains))
                    .map(String::valueOf)
                    .collect(joining(", "));
            throw new IllegalArgumentException("Unable to find authors with ids: " + missingAuthorIds + ".");
        }
        return new HashSet<>(authors);
    }
}
