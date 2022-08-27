package pl.szkolaspringa.bookstore.order.application.port;

import pl.szkolaspringa.bookstore.order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<Order> findAll();

    Optional<Order> findById(Long id);
}
