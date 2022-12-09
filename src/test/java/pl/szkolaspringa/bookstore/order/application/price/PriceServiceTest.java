package pl.szkolaspringa.bookstore.order.application.price;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({ PriceService.class, DeliveryDiscountStrategy.class, TotalPriceDiscountStrategy.class})
public class PriceServiceTest {

    @Autowired
    private PriceService priceService;

    @Test
    void calculatesTotalPriceOfEmptyOrder() {
        // given
        var order = Order.builder().build();

        // when
        var result = priceService.calculatePrice(order);

        // then
        assertThat(result.finalPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void calculatesTotalPrice() {
        // given
        var book1 = new Book();
        book1.setPrice(new BigDecimal("12.50"));
        var book2 = new Book();
        book2.setPrice(new BigDecimal("33.99"));
        var order = Order.builder()
                .item(new OrderItem(book1, 2))
                .item(new OrderItem(book2, 5))
                .build();

        // when
        var result = priceService.calculatePrice(order);

        // then
        assertThat(result.finalPrice()).isEqualTo(new BigDecimal("194.95"));
        assertThat(result.itemsPrice()).isEqualTo(new BigDecimal("194.95"));
    }
}