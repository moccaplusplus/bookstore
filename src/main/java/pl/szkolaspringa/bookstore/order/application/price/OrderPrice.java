package pl.szkolaspringa.bookstore.order.application.price;

import java.math.BigDecimal;

public record OrderPrice(BigDecimal itemsPrice, BigDecimal deliverPrice, BigDecimal discounts) {

    public static final OrderPrice ZERO = new OrderPrice(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    public BigDecimal finalPrice() {
        return itemsPrice().add(deliverPrice()).subtract(discounts());
    }
}
