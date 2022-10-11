package pl.szkolaspringa.bookstore.order.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("bookstore.orders")
public class OrderProperties {
    private final String abandonCron;
    private final Duration abandonAfter;
}
