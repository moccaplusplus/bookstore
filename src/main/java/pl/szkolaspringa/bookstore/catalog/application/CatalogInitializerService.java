package pl.szkolaspringa.bookstore.catalog.application;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogInitializerUseCase;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.Recipient;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogInitializerService implements CatalogInitializerUseCase {

    private final BookJpaRepository bookJpaRepository;
    private final AuthorJpaRepository authorJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final RestOperations restOperations;

    @Transactional
    @Override
    public void initialize() {
        initData();
        placeOrder();
    }

    @Override
    public void clear() {
        orderJpaRepository.deleteAll();
        bookJpaRepository.deleteAll();
        authorJpaRepository.deleteAll();
    }

    @SneakyThrows
    private void initData() {
        var resource = new ClassPathResource("/books.csv");
        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            var bookCsvStream = new CsvToBeanBuilder<BookCsv>(reader)
                    .withType(BookCsv.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse().stream();
            bookCsvStream.forEach(this::initBook);
        }
    }

    private void initBook(BookCsv bookCsv) {
        var resp = restOperations.getForEntity(bookCsv.getThumbnail(), byte[].class);
        var contentType = Optional.of(resp.getHeaders())
                .map(HttpHeaders::getContentType)
                .map(MediaType::toString)
                .orElse(null);
        var cover = new Upload(resp.getBody(), contentType, "cover");
        var authors = Arrays.stream(bookCsv.getAuthors().split(","))
                .filter(not(String::isBlank))
                .map(String::trim)
                .map(this::getOrCreateAuthor)
                .collect(toSet());
        var book = new Book();
        book.setTitle(bookCsv.getTitle());
        book.setAuthors(authors);
        book.setReleaseYear(bookCsv.getYear());
        book.setPrice(bookCsv.getAmount());
        book.setAvailable(50L);
        book.setCover(cover);
        bookJpaRepository.save(book);
    }

    private Author getOrCreateAuthor(String name) {
        return authorJpaRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            var p = name.lastIndexOf(" ");
            return p > 0 ? new Author(name.substring(0, p).trim(), name.substring(p + 1).trim()) :
                    new Author(null, name);
        });
    }

    private void placeOrder() {
        var book1 = bookJpaRepository.findOneByTitle("Effective Java").orElseThrow();
        var book2 = bookJpaRepository.findOneByTitle("Java Puzzlers").orElseThrow();
        var recipient = Recipient.builder()
                .name("Jan Kowalski")
                .phone("600-123-987")
                .street("ul. Długa 17")
                .city("Warszawa")
                .zipCode("00-950")
                .email("jan@dzban.com")
                .build();
        var order = Order.builder()
                .item(new OrderItem(book1, 16))
                .item(new OrderItem(book2, 7))
                .recipient(recipient)
                .build();
        order = orderJpaRepository.save(order);
        log.info("Created order with id: " + order.getId());
        orderJpaRepository.findAll().stream()
                .map(o -> "Got order with total price: " + o.totalPrice() + ", details: " + o)
                .forEach(log::info);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCsv {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private Integer year;
        @CsvBindByName
        private BigDecimal amount;
        @CsvBindByName
        private String thumbnail;
    }
}
