package pl.szkolaspringa.bookstore.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase.UpdateStatusCommand;
import pl.szkolaspringa.bookstore.order.application.port.QueryOrderUseCase;
import pl.szkolaspringa.bookstore.order.domain.Order;
import pl.szkolaspringa.bookstore.order.domain.OrderItem;
import pl.szkolaspringa.bookstore.order.domain.OrderStatus;
import pl.szkolaspringa.bookstore.order.domain.Recipient;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pl.szkolaspringa.bookstore.order.application.port.PlaceOrderUseCase.PlaceOrderCommand;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CatalogUseCase catalogUseCase;

    private final PlaceOrderUseCase placeOrderUseCase;

    private final QueryOrderUseCase queryOrderUseCase;

    @GetMapping
    public List<Order> getAll() {
        return queryOrderUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Order getOne(@PathVariable Long id) {
        return queryOrderUseCase.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createNewOrder(@Valid @RequestBody OrderDto dto) {
        var command = PlaceOrderCommand.builder()
                .items(dto.items().stream()
                        .map(item -> catalogUseCase.findOneById(item.bookId())
                                .map(book -> new OrderItem(book, item.quantity()))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .toList())
                .recipient(Recipient.builder()
                        .city(dto.recipient().city())
                        .email(dto.recipient().email())
                        .name(dto.recipient().name())
                        .phone(dto.recipient().phone())
                        .street(dto.recipient().street())
                        .zipCode(dto.recipient().zipCode())
                        .build())
                .build();
        var result = placeOrderUseCase.placeOrder(command);
        if (result.success()) {
            var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + result.orderId().toString()).build().toUri();
            return ResponseEntity.created(uri).build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusDto dto) {
        var result = placeOrderUseCase.updateStatus(new UpdateStatusCommand(id, dto.status()));
        if (!result.success()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeOrderById(@PathVariable Long id) {
        placeOrderUseCase.removeById(id);
    }

    public record OrderDto(@NotEmpty List<OrderItemDto> items, @NotNull RecipientDto recipient) {
    }

    public record OrderItemDto(@NotNull Long bookId, @DecimalMin("1") int quantity) {
    }

    public record RecipientDto(
            @NotBlank String name, @NotBlank String phone, @NotBlank String street, @NotBlank String city,
            @NotBlank String zipCode, @NotBlank String email) {
    }

    public record UpdateStatusDto(@NotNull OrderStatus status) {
    }
}
