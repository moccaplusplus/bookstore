package pl.szkolaspringa.bookstore.order.application.port;

import lombok.Builder;
import lombok.Singular;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;
import pl.szkolaspringa.bookstore.order.domain.Recipient;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public interface PlaceOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    UpdateStatusResponse updateStatus(UpdateStatusCommand command);

    void removeById(Long id);

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

    record UpdateStatusCommand(Long id, OrderStatus status) {
    }

    record UpdateStatusResponse(boolean success, List<String> errors) {
        public static final UpdateStatusResponse SUCCESS = new UpdateStatusResponse(true, emptyList());

        public static UpdateStatusResponse errorResponse(String... errors) {
            return new UpdateStatusResponse(false, Arrays.asList(errors));
        }
    }
}
