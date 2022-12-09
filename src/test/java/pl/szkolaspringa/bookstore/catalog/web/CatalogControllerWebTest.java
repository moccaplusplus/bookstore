package pl.szkolaspringa.bookstore.catalog.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
class CatalogControllerWebTest {

    @MockBean
    private CatalogUseCase catalogUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllBooks() throws Exception {
        // given
        var ejBook = new Book("Effective Java", 2005, new BigDecimal("99.00"), 50L);
        var jcipBook = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.00"), 50L);
        Mockito.when(catalogUseCase.findAllWithAuthors(isNull(), isNull())).thenReturn(List.of(ejBook, jcipBook));

        // expect
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}