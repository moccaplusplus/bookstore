package pl.szkolaspringa.bookstore.order.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.szkolaspringa.bookstore.order.application.port.RecipientUseCase;
import pl.szkolaspringa.bookstore.order.domain.Recipient;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/recipients")
@RequiredArgsConstructor
public class RecipientController {

    private final RecipientUseCase recipientUseCase;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Recipient> getAll() {
        return recipientUseCase.getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addRecipient(@Valid @RequestBody RecipientDto recipientDto) {
        var recipient = recipientUseCase.createRecipient(recipientDto);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().pathSegment(recipient.getId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRecipient(@PathVariable Long id, @RequestBody RecipientDto recipientDto) {
        recipientUseCase.updateRecipient(id, recipientDto);
        return ResponseEntity.accepted().build();
    }

    @Builder
    public record RecipientDto(
            @NotBlank String name, @NotBlank String phone, @NotBlank String street,
            @NotBlank String city, @NotBlank String zipCode, @NotBlank String email) {
    }
}
