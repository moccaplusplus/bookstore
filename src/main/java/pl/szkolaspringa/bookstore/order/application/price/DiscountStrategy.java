package pl.szkolaspringa.bookstore.order.application.price;

import pl.szkolaspringa.bookstore.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);

    default boolean isGreaterOrEqual(Order order, int threshold) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(threshold)) >= 0;
    }
}
