package pl.szkolaspringa.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.szkolaspringa.bookstore.catalog.domain.CatalogService;

@Component
public class ApplicationRunner implements CommandLineRunner {
    private final CatalogService catalogService;
    private final String query;
    private final long limit;

    public ApplicationRunner(
            CatalogService catalogService,
            @Value("${bookstore.catalog.query}") String query,
            @Value("${bookstore.catalog.limit}") int limit
    ) {
        this.catalogService = catalogService;
        this.query = query;
        this.limit = limit;
    }

    @Override
    public void run(String... args) {
//        var books = catalogService.findByTitle(query);
//        books.stream().limit(limit).forEach(System.out::println);

        System.out.println("Find by author: \"Henryk\"");
        var books = catalogService.findByAuthor("Henryk");
        books.forEach(System.out::println);
    }
}
