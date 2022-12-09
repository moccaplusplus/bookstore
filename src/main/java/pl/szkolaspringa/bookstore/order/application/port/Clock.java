package pl.szkolaspringa.bookstore.order.application.port;

import java.time.LocalDateTime;

public interface Clock {
    LocalDateTime now();
}
