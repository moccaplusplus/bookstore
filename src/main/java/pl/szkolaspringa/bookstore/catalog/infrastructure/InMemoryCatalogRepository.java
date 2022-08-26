package pl.szkolaspringa.bookstore.catalog.infrastructure;

import org.springframework.stereotype.Repository;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
class InMemoryCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong idNextValue = new AtomicLong(1L);

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void save(Book book) {
        if (book.getId() == null) {
            var id = idNextValue.getAndIncrement();
            book.setId(id);
            storage.put(id, book);
        } else {
            storage.put(book.getId(), book);
        }
    }

    @Override
    public void removeById(Long id) {
        storage.remove(id);
    }
}
