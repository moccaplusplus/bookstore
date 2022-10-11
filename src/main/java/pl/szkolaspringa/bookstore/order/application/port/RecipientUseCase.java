package pl.szkolaspringa.bookstore.order.application.port;

import pl.szkolaspringa.bookstore.order.domain.Recipient;
import pl.szkolaspringa.bookstore.order.web.RecipientController.RecipientDto;

import java.util.List;

public interface RecipientUseCase {
    List<Recipient> getAll();

    Recipient createRecipient(RecipientDto recipientDto);

    Recipient getOrCreateRecipient(RecipientDto recipientDto);

    Recipient updateRecipient(RecipientDto recipientDto);

    Recipient updateRecipient(Long id, RecipientDto recipientDto);
}
