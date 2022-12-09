package pl.szkolaspringa.bookstore.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.web.CatalogController.BookSaveDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerApiTest {

    @Autowired
    private CatalogUseCase catalogUseCase;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Test
    void getAllBooks() {
        // given
        var ejBook = new BookSaveDto("Effective Java", Set.of(), 2005, new BigDecimal("99.00"), 50L);
        var jcipBook = new BookSaveDto("Java Concurrency in Practice", Set.of(), 2006, new BigDecimal("99.00"), 50L);
        catalogUseCase.addBook(ejBook);
        catalogUseCase.addBook(jcipBook);

        // when
        var url = "http://localhost:" + port + "/catalog";
        var response = testRestTemplate.getForEntity(url, List.class);
        System.out.println(response.getBody());

        // then
        assertThat(response.getBody()).hasSize(2);
    }
}
