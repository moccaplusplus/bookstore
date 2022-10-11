package pl.szkolaspringa.bookstore.order.application.port;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.order.application.OrderProperties;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderProperties orderConfig;

    @Transactional
    @Scheduled(cron = "${bookstore.orders.abandon-cron}")
    public void run() {
        var duration = orderConfig.getAbandonAfter();
        var timestamp = LocalDateTime.now().minus(duration);
        var orders = orderJpaRepository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, timestamp);
        log.info("Orders count to be abandoned: " + orders.size());
        orders.forEach(order -> order.updateStatus(OrderStatus.ABANDONED));
    }
}
