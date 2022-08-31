package pl.szkolaspringa.bookstore;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.AddBookCommand;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase.PlaceOrderCommand;
import pl.szkolaspringa.bookstore.order.application.port.QueryOrderUseCase;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class KitchenSink {
    private final CatalogUseCase catalogUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final AuthorJpaRepository authorJpaRepository;

    public void initData() {
        var josh = new Author("Joshua", "Bloch");
        var neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(josh);
        authorJpaRepository.save(neal);
        catalogUseCase.addBook(new AddBookCommand("Effective Java", Set.of(josh.getId()), 2005, new BigDecimal("79.00")));
        catalogUseCase.addBook(new AddBookCommand("Java Puzzlers", Set.of(josh.getId(), neal.getId()), 2018, new BigDecimal("99.00")));
    }

    public void searchCatalog() {
        printByTitle();
        printByAuthor();
        findAndUpdate();
        printByTitle();
    }

    public void placeOrder() {
        var book1 = catalogUseCase.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalStateException("annot find a book"));
        var book2 = catalogUseCase.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalStateException("annot find a book"));
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

    private void printByTitle() {
        System.out.println("Find by title: \"Java\"");
        var books = catalogUseCase.findByTitle("Java");
        books.stream().limit(10).forEach(System.out::println);
    }

    private void printByAuthor() {
        System.out.println("Find by author: \"Josh\"");
        var books = catalogUseCase.findByAuthor("Josh");
        books.forEach(System.out::println);
    }

    private void findAndUpdate() {
        var status = catalogUseCase.findOneByTitleAndAuthor("Effective Java", "Joshua Bloch")
                .map(book -> UpdateBookCommand.builder()
                        .id(book.getId())
                        .price(new BigDecimal("59.00"))
                        .build())
                .map(catalogUseCase::updateBook)
                .map(UpdateBookResponse::success)
                .orElse(false);
        System.out.println("Updating book result: " + status);
    }
}
