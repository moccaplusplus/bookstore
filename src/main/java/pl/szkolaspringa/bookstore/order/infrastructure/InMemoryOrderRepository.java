package pl.szkolaspringa.bookstore.order.infrastructure;

import org.springframework.stereotype.Repository;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong idNextValue = new AtomicLong(1L);

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            var id = idNextValue.getAndIncrement();
            order.setId(id);
            order.setCreatedAt(LocalDateTime.now());
            storage.put(id, order);
        } else {
            storage.put(order.getId(), order);
        }
        return order;
    }

    @Override
    public void removeById(Long id) {
        storage.remove(id);
    }
}
