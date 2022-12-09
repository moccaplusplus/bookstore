package pl.szkolaspringa.bookstore.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CatalogControllerIntegrationTest {

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private CatalogController objectUnderTest;

    @Test
    void getAllBooks() {
        // given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();

        // when
        var result = objectUnderTest.getAll(null, null);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void getBooksByAuthor() {
        // given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();

        // when
        var result = objectUnderTest.getAll("Bloch", null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void getBooksByTitle() {
        // given
        givenEffectiveJava();
        givenJavaConcurrencyInPractice();

        // when
        var result = objectUnderTest.getAll(null, "Effective Java");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthors()).hasSize(1);
        assertThat(result.get(0).getAuthors().iterator().next().getLastName()).isEqualTo("Bloch");
    }

    private void givenEffectiveJava() {
        bookJpaRepository.save(Book.builder()
                .title("Effective Java")
                .author(new Author("Joshua", "Bloch"))
                .releaseYear(2005)
                .price(new BigDecimal("99.90"))
                .available(50L)
                .build());
    }

    private void givenJavaConcurrencyInPractice() {
        bookJpaRepository.save(Book.builder()
                .title("Java Concurrency in Practice")
                .author(new Author("Brian", "Goetz"))
                .releaseYear(2006)
                .price(new BigDecimal("129.90"))
                .available(50L)
                .build());
    }
}