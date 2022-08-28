package pl.szkolaspringa.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.order.application.port.QueryOrderUseCase;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Order;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueryOrderUseService implements QueryOrderUseCase {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }
}
