package pl.szkolaspringa.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.order.application.port.QueryOrderUseCase;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueryOrderUseService implements QueryOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
}
