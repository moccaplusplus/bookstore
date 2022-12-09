package pl.szkolaspringa.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.db.BookJpaRepository;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase;
import pl.szkolaspringa.bookstore.order.application.port.RecipientUseCase;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.db.RecipientJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;
import pl.szkolaspringa.bookstore.order.web.OrderController.OrderDto;

import javax.persistence.EntityNotFoundException;
import javax.validation.Validator;

@Transactional
@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final BookJpaRepository bookJpaRepository;

    private final OrderJpaRepository orderJpaRepository;

    private final RecipientJpaRepository recipientJpaRepository;

    private final RecipientUseCase recipientUseCase;

    private final Validator validator;

    @Override
    public Order placeOrder(OrderDto orderDto) {
        var items = orderDto.items().stream()
                .map(itemDto -> new OrderItem(
                        bookJpaRepository.findById(itemDto.bookId())
                                .orElseThrow(() -> new EntityNotFoundException("Book with id: " + itemDto.bookId() + " does not exist")),
                        itemDto.quantity()))
                .toList();
        var recipient = orderDto.recipient() == null ?
                recipientJpaRepository.getReferenceById(orderDto.recipientId()) :
                recipientUseCase.getOrCreateRecipient(orderDto.recipient());
        var order = Order.builder()
                .items(items)
                .recipient(recipient)
                .delivery(orderDto.delivery())
                .build();
        validator.validate(order);
        order = orderJpaRepository.saveAndFlush(order);
        order.getItems().forEach(orderItem -> orderItem.getBook().removeAvailable(orderItem.getQuantity()));
        return order;
    }

    @Override
    public Order updateStatus(Long id, OrderStatus status) {
        var order = orderJpaRepository.findById(id).orElseThrow();
        order.updateStatus(status);
        if (status.shouldRevokeBooks()) {
            order.getItems().forEach(orderItem -> orderItem.getBook().addAvailable(orderItem.getQuantity()));
        }
        return order;
    }

    @Override
    public void removeById(Long id) {
        orderJpaRepository.deleteById(id);
    }
}
