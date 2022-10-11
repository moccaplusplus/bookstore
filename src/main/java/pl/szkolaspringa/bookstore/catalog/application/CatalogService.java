package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.catalog.web.CatalogController.BookSaveDto;
import pl.szkolaspringa.bookstore.upload.db.UploadJpaRepository;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final AuthorJpaRepository authorJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final UploadJpaRepository uploadJpaRepository;

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
    public Book addBook(BookSaveDto dto) {
        var book = new Book(dto.title(), dto.year(), dto.price(), dto.available());
        book.setAuthors(dto.authors().stream().map(authorJpaRepository::getReferenceById).collect(toSet()));
//        var authors = fetchAuthorsByIds(command.authors());
//        authors.forEach(book::addAuthor);
        return bookJpaRepository.save(book);
    }

    @Transactional
    @Override
    public void updateBook(Long id, BookSaveDto dto) {
        var book = bookJpaRepository.findById(id).orElseThrow();
        Optional.of(dto).map(BookSaveDto::title).ifPresent(book::setTitle);
        Optional.of(dto).map(BookSaveDto::year).ifPresent(book::setReleaseYear);
        Optional.of(dto).map(BookSaveDto::price).ifPresent(book::setPrice);
        Optional.of(dto).map(BookSaveDto::available).ifPresent(book::setAvailable);
        Optional.of(dto).map(BookSaveDto::authors)
                .map(authorIds -> authorIds.stream().map(authorJpaRepository::getReferenceById).collect(toSet()))
                .ifPresent(book::setAuthors);
    }

    @Transactional
    @Override
    public void removeById(Long id) {
        bookJpaRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateBookCover(Long id, FileInfo fileInfo) {
        var book = bookJpaRepository.findById(id).orElseThrow();
        if (book.getCover() == null) book.setCover(new Upload());
        var cover = book.getCover();
        cover.setFile(fileInfo.file());
        cover.setContentType(fileInfo.contentType());
        cover.setFilename(fileInfo.fileName());
    }

    @Transactional
    @Override
    public void removeBookCover(Long id) {
        var book = bookJpaRepository.findById(id).orElseThrow();
        var cover = book.getCover();
        if (cover != null) {
            book.setCover(null);
            uploadJpaRepository.delete(cover);
        }
    }

    private List<Author> fetchAuthorsByIds(Collection<Long> authorIds) {
        var authors = authorJpaRepository.findAllById(authorIds);
        log.info("Authors: " + authors);
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
