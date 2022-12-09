package pl.szkolaspringa.bookstore.order.application.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.order.domain.Order;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final List<DiscountStrategy> strategies;

    public OrderPrice calculatePrice(Order order) {
        if (order.getItems().isEmpty()) {
            return OrderPrice.ZERO;
        }
        return new OrderPrice(order.getItemsPrice(), order.getDelivery().getPrice(), calculateDiscounts(order));
    }

    private BigDecimal calculateDiscounts(Order order) {
        return strategies.stream().map(s -> s.calculate(order)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
