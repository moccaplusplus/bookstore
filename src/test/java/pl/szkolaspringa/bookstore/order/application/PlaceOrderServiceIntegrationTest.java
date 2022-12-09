package pl.szkolaspringa.bookstore.order.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.order.application.price.DeliveryDiscountStrategy;
import pl.szkolaspringa.bookstore.order.application.price.PriceService;
import pl.szkolaspringa.bookstore.order.application.price.TotalPriceDiscountStrategy;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Delivery;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;
import pl.szkolaspringa.bookstore.order.web.OrderController.OrderDto;
import pl.szkolaspringa.bookstore.order.web.OrderController.OrderItemDto;
import pl.szkolaspringa.bookstore.order.web.RecipientController.RecipientDto;
import pl.szkolaspringa.bookstore.upload.application.StorageService;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({
        PlaceOrderService.class, StorageService.class, RecipientService.class,
        PriceService.class, DeliveryDiscountStrategy.class, TotalPriceDiscountStrategy.class})
class PlaceOrderServiceIntegrationTest {

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private PriceService priceService;

    @Autowired
    private PlaceOrderService objectUnderTest;

    @Test
    void userCanPlaceOrder() {
        // given
        var ejBook = givenEffectiveJava(50L);
        var jcipBook = givenJavaConcurrencyInPractice(50L);
        var orderDto = orderDto(
                new OrderItemDto(ejBook.getId(), 15),
                new OrderItemDto(jcipBook.getId(), 10));

        // when
        var result = objectUnderTest.placeOrder(orderDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(availableCopies(ejBook.getId())).isEqualTo(35);
        assertThat(availableCopies(jcipBook.getId())).isEqualTo(40);
    }

    @Test
    void userCannotOrderMoreBooksThanAvailable() {
        // given
        var ejBook = givenEffectiveJava(5L);
        var orderDto = orderDto(new OrderItemDto(ejBook.getId(), 10));

        // when
        var thrown = assertThatThrownBy(() -> objectUnderTest.placeOrder(orderDto));

        // then
        thrown
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Requested 10 of available 5 copies of book: " + ejBook.getId() + ".");
    }

    @Test
    void userCanRevokeOrder() {
        // given
        var ejBook = givenEffectiveJava(50L);
        var orderDto = orderDto(new OrderItemDto(ejBook.getId(), 15));
        var order = objectUnderTest.placeOrder(orderDto);
        assertThat(availableCopies(ejBook.getId())).isEqualTo(35);

        // when
        objectUnderTest.updateStatus(order.getId(), OrderStatus.CANCELLED);

        // then
        assertThat(availableCopies(ejBook.getId())).isEqualTo(50L);
        assertThat(orderStatus(order.getId())).isEqualTo(OrderStatus.CANCELLED);
    }

    //    @Disabled("homework")
    @Test
    void userCannotRevokePaidOrder() {
        // given
        var ejBook = givenEffectiveJava(50L);
        var orderDto = orderDto(new OrderItemDto(ejBook.getId(), 15));
        var order = objectUnderTest.placeOrder(orderDto);
        objectUnderTest.updateStatus(order.getId(), OrderStatus.PAID);

        // when
        var thrown = assertThatThrownBy(() -> objectUnderTest.updateStatus(order.getId(), OrderStatus.CANCELLED));

        // then
        thrown
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transition from PAID to CANCELLED is forbidden");
    }

    //    @Disabled("homework")
    @Test
    void userCannotRevokeShippedOrder() {
        // given
        var ejBook = givenEffectiveJava(50L);
        var orderDto = orderDto(new OrderItemDto(ejBook.getId(), 15));
        var order = objectUnderTest.placeOrder(orderDto);
        objectUnderTest.updateStatus(order.getId(), OrderStatus.PAID);
        objectUnderTest.updateStatus(order.getId(), OrderStatus.SHIPPED);

        // when
        var thrown = assertThatThrownBy(() -> objectUnderTest.updateStatus(order.getId(), OrderStatus.CANCELLED));

        // then
        thrown
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transition from SHIPPED to CANCELLED is forbidden");
    }

    //    @Disabled("homework")
    @Test
    void userCannotOrderNonExistingBook() {
        // given
        var nonExitingBookId = 10000L;
        var orderDto = orderDto(new OrderItemDto(nonExitingBookId, 1));

        // when
        var thrown = assertThatThrownBy(() -> objectUnderTest.placeOrder(orderDto));

        // then
        thrown
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book with id: " + nonExitingBookId + " does not exist");
    }

    //    @Disabled("homework")
    @Test
    void userCannotOrderNegativeNumberOfBook() {
        // given
        var ejBook = givenEffectiveJava(5L);
        var orderDto = orderDto(new OrderItemDto(ejBook.getId(), -5));

        // when
        var thrown = assertThatThrownBy(() -> objectUnderTest.placeOrder(orderDto));

        // then
        thrown
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("must be greater than 0");
    }

    @Test
    void shippingCostsAreAddedToTotalOrderPrice() {
        // given
        var book = givenJavaConcurrencyInPractice(50L, "49.90");
        var orderDto = orderDto(new OrderItemDto(book.getId(), 1));

        // when
        var order = objectUnderTest.placeOrder(orderDto);
        var orderPrice = priceService.calculatePrice(order);

        // then
        assertThat(orderPrice.finalPrice().toPlainString()).isEqualTo("59.80");
    }

    @Test
    void cheapestBookIsHalfPricedWhenTotalOver100Zloty() {
        // given
        var book = givenJavaConcurrencyInPractice(50L, "49.90");
        var orderDto = orderDto(new OrderItemDto(book.getId(), 3));

        // when
        var order = objectUnderTest.placeOrder(orderDto);
        var orderPrice = priceService.calculatePrice(order);

        // then
        assertThat(orderPrice.finalPrice().toPlainString()).isEqualTo("149.70");
        assertThat(orderPrice.itemsPrice().toPlainString()).isEqualTo("149.70");
    }

    @Test
    void cheapestBookIsHalfPricedWhenTotalOver200Zloty() {
        // given
        var book = givenJavaConcurrencyInPractice(50L, "49.90");
        var orderDto = orderDto(new OrderItemDto(book.getId(), 5));

        // when
        var order = objectUnderTest.placeOrder(orderDto);
        var orderPrice = priceService.calculatePrice(order);

        // then
        assertThat(orderPrice.finalPrice().toPlainString()).isEqualTo("224.55");
    }

    @Test
    void cheapestBookIsFreeWhenTotalOver400Zloty() {
        // given
        var book = givenJavaConcurrencyInPractice(50L, "49.90");
        var orderDto = orderDto(new OrderItemDto(book.getId(), 10));

        // when
        var order = objectUnderTest.placeOrder(orderDto);
        var orderPrice = priceService.calculatePrice(order);

        // then
        assertThat(orderPrice.finalPrice().toPlainString()).isEqualTo("449.10");
    }

    @Disabled("security")
    @Test
    void userCannotRevokeOtherUsersOrder() {
        // TODO: add when security is enabled
    }

    @Disabled("security")
    @Test
    void adminCannotRevokeOtherUsersOrder() {
        // TODO: add when security is enabled
    }

    @Disabled("security")
    @Test
    void adminCanMarkOrderAsPaid() {
        // TODO: add when security is enabled
    }

    private OrderStatus orderStatus(Long id) {
        return orderJpaRepository.getReferenceById(id).getStatus();
    }

    private long availableCopies(Long bookId) {
        return bookJpaRepository.findById(bookId).orElseThrow().getAvailable();
    }

    private Book givenEffectiveJava(long available) {
        return bookJpaRepository.save(Book.builder()
                .title("Effective Java")
                .author(new Author("Joshua", "Bloch"))
                .releaseYear(2005)
                .price(new BigDecimal("99.90"))
                .available(available)
                .build());
    }

    private Book givenJavaConcurrencyInPractice(long available) {
        return givenJavaConcurrencyInPractice(available, "129.90");
    }

    private Book givenJavaConcurrencyInPractice(long available, String price) {
        return bookJpaRepository.save(Book.builder()
                .title("Java Concurrency in Practice")
                .author(new Author("Brian", "Goetz"))
                .releaseYear(2006)
                .price(new BigDecimal(price))
                .available(available)
                .build());
    }

    private OrderDto orderDto(OrderItemDto... items) {
        return OrderDto.builder()
                .recipient(recipientDto())
                .items(List.of(items))
                .delivery(Delivery.COURIER)
                .build();
    }

    private RecipientDto recipientDto() {
        return RecipientDto.builder()
                .name("Jan Kowalski")
                .phone("600-123-987")
                .street("ul. Długa 17")
                .city("Warszawa")
                .zipCode("00-950")
                .email("jan@dzban.com")
                .build();
    }
}