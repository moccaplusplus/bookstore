package pl.szkolaspringa.bookstore.order.domain;

import java.util.List;

public interface OrderRepository {
    List<Order> findAll();
    Order save(Order order);
}
