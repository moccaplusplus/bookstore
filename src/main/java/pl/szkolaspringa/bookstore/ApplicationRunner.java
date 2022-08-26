package pl.szkolaspringa.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.AddBookCommand;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase.PlaceOrderCommand;
import pl.szkolaspringa.bookstore.order.application.port.QueryOrderUseCase;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.Recipient;

import java.math.BigDecimal;

@Component
public class ApplicationRunner implements CommandLineRunner {
    private final CatalogUseCase catalogUseCase;
    private final String query;
    private final long limit;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;

    public ApplicationRunner(
            CatalogUseCase catalogUseCase,
            PlaceOrderUseCase placeOrderUseCase,
            QueryOrderUseCase queryOrderUseCase,
            @Value("${bookstore.catalog.query}") String query,
            @Value("${bookstore.catalog.limit}") int limit
    ) {
        this.catalogUseCase = catalogUseCase;
        this.placeOrderUseCase = placeOrderUseCase;
        this.queryOrderUseCase = queryOrderUseCase;
        this.query = query;
        this.limit = limit;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void initData() {
        catalogUseCase.addBook(new AddBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, new BigDecimal("35.50")));
        catalogUseCase.addBook(new AddBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884, new BigDecimal("42.0")));
        catalogUseCase.addBook(new AddBookCommand("Chłopi", "Władysław Reymont", 1904, new BigDecimal("50.0")));
        catalogUseCase.addBook(new AddBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1899, new BigDecimal("45.55")));

        catalogUseCase.addBook(new AddBookCommand("Przygoda Fryzjera Damskiego", "Eduardo Mendoza", 2001, new BigDecimal("33.60")));
        catalogUseCase.addBook(new AddBookCommand("Rzeźnia nr 5", "Kurt Vonnegut Jr", 1969, new BigDecimal("36.90")));
        catalogUseCase.addBook(new AddBookCommand("Paw Królowej", "Dorota Masłowska", 2005, new BigDecimal("29.90")));
        catalogUseCase.addBook(new AddBookCommand("Nowy Wspaniały Świat", "Aldous Huxley", 1931, new BigDecimal("42.35")));
    }

    private void searchCatalog() {
        printByTitle();
//        printByAuthor();
        findAndUpdate();
        printByTitle();
    }

    private void printByTitle() {
        var books = catalogUseCase.findByTitle(query);
        books.stream().limit(limit).forEach(System.out::println);
    }

    private void printByAuthor() {
        System.out.println("Find by author: \"Henryk\"");
        var books = catalogUseCase.findByAuthor("Henryk");
        books.forEach(System.out::println);
    }

    private void findAndUpdate() {
        var status = catalogUseCase.findByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .map(book -> UpdateBookCommand.builder()
                        .id(book.getId())
                        .title("Pan Tadeusz, czyli Ostatni Zajazd na Litwie")
                        .build())
                .map(catalogUseCase::updateBook)
                .map(UpdateBookResponse::success)
                .orElse(false);
        System.out.println("Updating book result: " + status);
    }

    private void placeOrder() {
        var book1 = catalogUseCase.findOneByTitle("Pan Tadeusz").orElseThrow(() -> new IllegalStateException("annot find a book"));
        var book2 = catalogUseCase.findOneByTitle("Chłopi").orElseThrow(() -> new IllegalStateException("annot find a book"));
        var recipient = Recipient.builder()
                .name("Jan Kowalski")
                .phone("600-123-987")
                .street("ul. Długa 17")
                .city("Warszawa")
                .zipCode("00-950")
                .email("jan@dzban.com")
                .build();
        var command = PlaceOrderCommand.builder()
                .item(new OrderItem(book1, 16))
                .item(new OrderItem(book2, 7))
                .recipient(recipient)
                .build();
        var result = placeOrderUseCase.placeOrder(command);
        System.out.println("Created order with id: " + result.orderId());
        queryOrderUseCase.findAll()
                .forEach(order -> System.out.println("Got order with total price: " + order.totalPrice() + ", details: " + order));
    }
}
