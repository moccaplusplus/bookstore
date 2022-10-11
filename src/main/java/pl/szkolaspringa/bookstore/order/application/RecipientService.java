package pl.szkolaspringa.bookstore.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.order.application.port.RecipientUseCase;
import pl.szkolaspringa.bookstore.order.db.RecipientJpaRepository;
import pl.szkolaspringa.bookstore.order.domain.Recipient;
import pl.szkolaspringa.bookstore.order.web.RecipientController.RecipientDto;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class RecipientService implements RecipientUseCase {

    private final RecipientJpaRepository recipientJpaRepository;

    @Override
    public List<Recipient> getAll() {
        return recipientJpaRepository.findAll();
    }

    @Override
    public Recipient createRecipient(RecipientDto recipientDto) {
        var recipient = Recipient.builder()
                .city(recipientDto.city())
                .email(recipientDto.email())
                .name(recipientDto.name())
                .phone(recipientDto.phone())
                .street(recipientDto.street())
                .zipCode(recipientDto.zipCode())
                .build();
        return recipientJpaRepository.save(recipient);
    }

    @Override
    public Recipient getOrCreateRecipient(RecipientDto recipientDto) {
        var recipient = recipientJpaRepository.findByEmailIgnoreCase(recipientDto.email())
                .orElseGet(() -> Recipient.builder().email(recipientDto.email()).build());
        updateFields(recipient, recipientDto);
        return recipient;
    }

    @Override
    public Recipient updateRecipient(RecipientDto recipientDto) {
        var recipient = recipientJpaRepository.findByEmailIgnoreCase(recipientDto.email()).orElseThrow();
        updateFields(recipient, recipientDto);
        return recipient;
    }

    @Override
    public Recipient updateRecipient(Long id, RecipientDto recipientDto) {
        var recipient = recipientJpaRepository.findById(id).orElseThrow();
        updateFields(recipient, recipientDto);
        Optional.of(recipientDto).map(RecipientDto::email).ifPresent(recipient::setEmail);
        return recipient;
    }

    private void updateFields(Recipient recipient, RecipientDto recipientDto) {
        Optional.of(recipientDto).map(RecipientDto::city).ifPresent(recipient::setCity);
        Optional.of(recipientDto).map(RecipientDto::name).ifPresent(recipient::setName);
        Optional.of(recipientDto).map(RecipientDto::phone).ifPresent(recipient::setPhone);
        Optional.of(recipientDto).map(RecipientDto::street).ifPresent(recipient::setStreet);
        Optional.of(recipientDto).map(RecipientDto::zipCode).ifPresent(recipient::setZipCode);
    }
}
