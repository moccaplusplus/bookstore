package pl.szkolaspringa.bookstore.catalog.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public List<Book> findByAuthor(String name) {
        return catalogRepository.listAll().stream()
                .filter(book -> book.getAuthor().startsWith(name))
                .collect(Collectors.toList());
    }

    public List<Book> findByTitle(String title) {
        return catalogRepository.listAll().stream()
                .filter(book -> book.getTitle().startsWith(title))
                .collect(Collectors.toList());
    }
}
