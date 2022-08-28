package pl.szkolaspringa.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase;
import pl.szkolaspringa.bookstore.order.db.OrderJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Order;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        var order = Order.builder()
                .recipient(command.recipient())
                .items(command.items())
                .build();
        orderJpaRepository.save(order);
        return PlaceOrderResponse.successResponse(order.getId());
    }

    @Override
    public UpdateStatusResponse updateStatus(UpdateStatusCommand command) {
        return orderJpaRepository.findById(command.id()).map(order -> {
            order.setStatus(command.status());
            orderJpaRepository.save(order);
            return UpdateStatusResponse.SUCCESS;
        }).orElseGet(() -> UpdateStatusResponse.errorResponse("Order not found"));
    }

    @Override
    public void removeById(Long id) {
        orderJpaRepository.deleteById(id);
    }
}
