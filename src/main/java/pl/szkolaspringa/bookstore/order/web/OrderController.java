package pl.szkolaspringa.bookstore.order.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase;
import pl.szkolaspringa.bookstore.order.application.port.QueryOrderUseCase;
import pl.szkolaspringa.bookstore.order.application.price.OrderPrice;
import pl.szkolaspringa.bookstore.order.application.price.PriceService;
import pl.szkolaspringa.bookstore.order.domain.Delivery;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pl.szkolaspringa.bookstore.order.web.RecipientController.RecipientDto;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CatalogUseCase catalogUseCase;

    private final PlaceOrderUseCase placeOrderUseCase;

    private final QueryOrderUseCase queryOrderUseCase;

    private final PriceService priceService;

    @Transactional(readOnly = true)
    @GetMapping
    public List<OrderInfoDto> getAll() {
        return queryOrderUseCase.findAll().stream()
                .map(order -> OrderInfoDto.of(order, priceService.calculatePrice(order)))
                .toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public OrderInfoDto getOne(@PathVariable Long id) {
        return queryOrderUseCase.findById(id)
                .map(order -> OrderInfoDto.of(order, priceService.calculatePrice(order)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createNewOrder(@Valid @RequestBody OrderDto orderDto) {
        if (orderDto.recipient() == null && orderDto.recipientId() == null) {
            throw new ValidationException("Either recipient data or id should be present");
        }
        var order = placeOrderUseCase.placeOrder(orderDto);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().pathSegment(order.getId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusDto dto) {
        placeOrderUseCase.updateStatus(id, dto.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeOrderById(@PathVariable Long id) {
        placeOrderUseCase.removeById(id);
    }

    @Builder
    public record OrderDto(@Singular @NotEmpty List<OrderItemDto> items, RecipientDto recipient, Long recipientId, Delivery delivery) {
    }

    public record OrderInfoDto(Long id, OrderStatus orderStatus, List<OrderItemDto> items, Long recipientId, OrderPrice orderPrice) {
        public static OrderInfoDto of(Order order, OrderPrice orderPrice) {
            return new OrderInfoDto(
                    order.getId(),
                    order.getStatus(),
                    order.getItems().stream().map(OrderItemDto::of).toList(),
                    order.getRecipient().getId(),
                    orderPrice);
        }
    }

    public record OrderItemDto(@NotNull Long bookId, @DecimalMin("1") int quantity) {
        public static OrderItemDto of(OrderItem orderItem) {
            return new OrderItemDto(orderItem.getBook().getId(), orderItem.getQuantity());
        }
    }

    public record UpdateStatusDto(@NotNull OrderStatus status) {
    }

}
