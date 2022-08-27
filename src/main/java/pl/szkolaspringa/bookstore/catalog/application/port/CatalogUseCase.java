package pl.szkolaspringa.bookstore.catalog.application.port;

import lombok.Builder;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {

    List<Book> findAll();

    List<Book> findAllByAuthor(String name);

    List<Book> findAllByTitle(String title);

    List<Book> findAllByTitleAndAuthor(String title, String author);

    Optional<Book> findOneById(Long id);

    Optional<Book> findOneByTitle(String title);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    Book addBook(AddBookCommand command);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void removeById(Long id);

    void updateBookCover(UpdateBookCoverCommand command);

    void removeBookCover(Long id);

    record AddBookCommand(String title, String author, Integer year, BigDecimal price) {
    }

    @Builder
    record UpdateBookCommand(Long id, String title, String author, Integer year, BigDecimal price) {
        public void updateBook(Book book) {
            Optional.ofNullable(title).ifPresent(book::setTitle);
            Optional.ofNullable(author).ifPresent(book::setAuthor);
            Optional.ofNullable(year).ifPresent(book::setYear);
            Optional.ofNullable(price).ifPresent(book::setPrice);
        }
    }

    record UpdateBookResponse(boolean success, List<String> errors) {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());

        public static UpdateBookResponse errorResponse(String... errors) {
            return new UpdateBookResponse(false, Arrays.asList(errors));
        }
    }

    record UpdateBookCoverCommand(Long id, byte[] file, String contentType, String fileName) {
    }
}
