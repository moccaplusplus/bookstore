package pl.szkolaspringa.bookstore;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogInitializerUseCase;

@RequestMapping("/admin")
@RestController
@AllArgsConstructor
public class AdminController {

    private final CatalogInitializerUseCase catalogInitializer;

    @PostMapping("/init")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void onApplicationReady() {
        catalogInitializer.initialize();
    }

    @PostMapping("/clear")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void clear() {
        catalogInitializer.clear();
    }
}
