package pl.szkolaspringa.bookstore.catalog.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szkolaspringa.bookstore.catalog.application.port.AuthorUseCase;
import pl.szkolaspringa.bookstore.catalog.domain.Author;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorsController {

    private final AuthorUseCase authorUseCase;

    @GetMapping
    public List<Author> getAll() {
        return authorUseCase.findAllEager();
    }
}
