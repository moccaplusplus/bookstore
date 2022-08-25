package pl.szkolaspringa.bookstore.catalog.domain;

import java.util.List;

public interface CatalogRepository {
    List<Book> listAll();
}
