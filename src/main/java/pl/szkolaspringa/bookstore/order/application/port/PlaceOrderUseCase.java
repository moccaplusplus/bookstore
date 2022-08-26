package pl.szkolaspringa.bookstore.order.application.port;

import lombok.Builder;
import lombok.Singular;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.Recipient;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public interface PlaceOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    @Builder
    record PlaceOrderCommand(@Singular List<OrderItem> items, Recipient recipient) {
    }

    record PlaceOrderResponse(boolean success, Long orderId, List<String> errors) {
        public static PlaceOrderResponse successResponse(Long orderId) {
            return new PlaceOrderResponse(true, orderId, emptyList());
        }

        public static PlaceOrderResponse errorResponse(String... errors) {
            return new PlaceOrderResponse(false, null, Arrays.asList(errors));
        }
    }
}
