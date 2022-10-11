package pl.szkolaspringa.bookstore.order.application.port;

import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;
import pl.szkolaspringa.bookstore.order.web.OrderController.OrderDto;

public interface PlaceOrderUseCase {

    Order placeOrder(OrderDto command);

    Order updateStatus(Long id, OrderStatus status);

    void removeById(Long id);
}
