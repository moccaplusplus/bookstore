package pl.szkolaspringa.bookstore.order.application.price;

import org.springframework.stereotype.Component;
import pl.szkolaspringa.bookstore.order.domain.Order;

import java.math.BigDecimal;

@Component
public class DeliveryDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        if (isGreaterOrEqual(order, 100)) {
            return order.getDelivery().getPrice();
        }
        return BigDecimal.ZERO;
    }
}
