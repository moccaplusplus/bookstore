package pl.szkolaspringa.bookstore.order.application.port;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Delivery;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;
import pl.szkolaspringa.bookstore.order.web.OrderController;
import pl.szkolaspringa.bookstore.order.web.RecipientController.RecipientDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "bookstore.orders.abandon-after=1H")
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class FakeClock {

        private static LocalDateTime time;

        @Bean
        @Primary
        Clock fakeClock() {
            return () -> time;
        }
    }

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private PlaceOrderUseCase placeOrderUseCase;

    @Autowired
    private AbandonedOrdersJob objectUnderTest;

    @Test
    void shouldMarkOrdersAsAbandoned() {
        // given
        var ejBook = givenEffectiveJava(50L);
        var orderDto = OrderController.OrderDto.builder()
                .recipient(recipientDto())
                .item(new OrderController.OrderItemDto(ejBook.getId(), 15))
                .delivery(Delivery.COURIER)
                .build();
        var order = placeOrderUseCase.placeOrder(orderDto);

        // when
        FakeClock.time = LocalDateTime.now().plusHours(2);
        objectUnderTest.run();

        // then
        assertThat(orderStatus(order.getId())).isEqualTo(OrderStatus.ABANDONED);
    }

    private OrderStatus orderStatus(Long id) {
        return orderJpaRepository.findById(id).map(Order::getStatus).orElseThrow();
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