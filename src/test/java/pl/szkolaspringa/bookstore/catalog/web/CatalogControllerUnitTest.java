package pl.szkolaspringa.bookstore.catalog.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {CatalogController.class})
@ExtendWith(MockitoExtension.class)
class CatalogControllerUnitTest {

    //@MockBean
    @Mock
    private CatalogUseCase catalogUseCase;

    //@Autowired
    @InjectMocks
    private CatalogController objectUnderTest;

    @Test
    void shouldGetAllBooks() {
        // given
        var ejBook = Book.builder().title("Effective Java").build();
        var jcipBook = Book.builder().title("Java Concurrency in Practice").build();
        when(catalogUseCase.findAllWithAuthors(isNull(), isNull())).thenReturn(List.of(ejBook, jcipBook));

        // when
        var result = objectUnderTest.getAll(null, null);

        // then
        assertThat(result).hasSize(2);
    }
}