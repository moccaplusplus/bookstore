package pl.szkolaspringa.bookstore.order.application.price;

import org.springframework.stereotype.Component;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        if (isGreaterOrEqual(order, 400)) {
            return getCheapestBookPrice(order);
        }
        if (isGreaterOrEqual(order, 200)) {
            return getCheapestBookPrice(order).divide(BigDecimal.valueOf(2), RoundingMode.HALF_DOWN);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getCheapestBookPrice(Order order) {
        return order.getItems().stream()
                .map(OrderItem::getBook)
                .map(Book::getPrice)
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}
