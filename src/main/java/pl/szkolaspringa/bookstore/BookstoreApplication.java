package pl.szkolaspringa.bookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookstoreApplication implements CommandLineRunner {

    @Autowired
    private KitchenSink kitchenSink;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        kitchenSink.initData();
        kitchenSink.searchCatalog();
        kitchenSink.placeOrder();
    }
}
