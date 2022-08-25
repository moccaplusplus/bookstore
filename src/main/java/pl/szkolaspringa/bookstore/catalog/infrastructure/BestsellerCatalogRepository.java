package pl.szkolaspringa.bookstore.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class BestsellerCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    public BestsellerCatalogRepository() {
        storage.put(1L, new Book(1L, "Przygoda Fryzjera Damskiego", "Eduardo Mendoza", 2001));
        storage.put(2L, new Book(2L, "Rzeźnia nr 5", "Kurt Vonnegut Jr", 1969));
        storage.put(3L, new Book(3L, "Paw Królowej", "Dorota Masłowska", 2005));
        storage.put(3L, new Book(4L, "Nowy Wspaniały Świat", "Aldous Huxley", 1931));
    }

    @Override
    public List<Book> listAll() {
        return new ArrayList<>(storage.values());
    }
}
