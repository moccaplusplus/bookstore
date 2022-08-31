package pl.szkolaspringa.bookstore.catalog.application.port;

import pl.szkolaspringa.bookstore.catalog.domain.Author;

import java.util.List;

public interface AuthorUseCase {
    List<Author> findAll();

    List<Author> findAllEager();
}
