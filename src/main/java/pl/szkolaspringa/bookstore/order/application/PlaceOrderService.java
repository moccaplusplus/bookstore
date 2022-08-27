package pl.szkolaspringa.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderRepository;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        var order = Order.builder()
                .recipient(command.recipient())
                .items(command.items())
                .build();
        orderRepository.save(order);
        return PlaceOrderResponse.successResponse(order.getId());
    }

    @Override
    public UpdateStatusResponse updateStatus(UpdateStatusCommand command) {
        return orderRepository.findById(command.id()).map(order -> {
            order.setStatus(command.status());
            orderRepository.save(order);
            return UpdateStatusResponse.SUCCESS;
        }).orElseGet(() -> UpdateStatusResponse.errorResponse("Order not found"));
    }

    @Override
    public void removeById(Long id) {
        orderRepository.removeById(id);
    }
}
