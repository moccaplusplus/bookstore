package pl.szkolaspringa.bookstore.catalog.application.port;

import lombok.Builder;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.catalog.web.CatalogController.BookSaveDto;

import java.util.List;
import java.util.Optional;

public interface CatalogUseCase {

    List<Book> findAll();

    List<Book> findByAuthor(String name);

    List<Book> findByTitle(String title);

    List<Book> findByTitleAndAuthor(String title, String author);

    Optional<Book> findOneById(Long id);

    Optional<Book> findOneByTitle(String title);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    List<Book> findAllWithAuthors(String title, String author);

    @Transactional(readOnly = true)
    Optional<Book> findOneWithAuthors(Long id);

    Book addBook(BookSaveDto command);

    void updateBook(Long id, BookSaveDto dto);

    void removeById(Long id);

    void updateBookCover(Long id, FileInfo command);

    void removeBookCover(Long id);

    @Builder
    record FileInfo(byte[] file, String contentType, String fileName) {
    }
}
