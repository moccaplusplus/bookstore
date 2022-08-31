package pl.szkolaspringa.bookstore.catalog.application.port;

import lombok.Builder;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;

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

    Book addBook(AddBookCommand command);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void removeById(Long id);

    void updateBookCover(UpdateBookCoverCommand command);

    void removeBookCover(Long id);

    record AddBookCommand(String title, Set<Long> authors, Integer year, BigDecimal price) {
    }

    @Builder
    record UpdateBookCommand(Long id, String title, Set<Long> authors, Integer year, BigDecimal price) {
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
