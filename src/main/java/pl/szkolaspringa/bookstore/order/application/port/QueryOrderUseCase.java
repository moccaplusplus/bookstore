package pl.szkolaspringa.bookstore.order.application.port;

import pl.szkolaspringa.bookstore.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
