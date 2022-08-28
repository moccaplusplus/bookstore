package pl.szkolaspringa.bookstore.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
