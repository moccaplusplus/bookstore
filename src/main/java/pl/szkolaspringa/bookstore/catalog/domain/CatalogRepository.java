package pl.szkolaspringa.bookstore.catalog.domain;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Book save(Book book);

    void removeById(Long id);
}
