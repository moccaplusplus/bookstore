package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.catalog.domain.CatalogRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final CatalogRepository catalogRepository;

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Override
    public List<Book> findByAuthor(String name) {
        return catalogRepository.findAll().stream()
                .filter(book -> book.getAuthor().startsWith(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findAll().stream()
                .filter(book -> book.getTitle().startsWith(title))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll().stream()
                .filter(book -> book.getTitle().startsWith(title))
                .filter(book -> book.getAuthor().startsWith(author))
                .findAny();
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return catalogRepository.findAll().stream()
                .filter(book -> book.getTitle().startsWith(title))
                .findAny();
    }

    @Override
    public void addBook(AddBookCommand command) {
        var book = new Book(command.title(), command.author(), command.year(), command.price());
        catalogRepository.save(book);
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        var found = catalogRepository.findById(command.id());
        if (found.isEmpty()) return UpdateBookResponse.errorResponse("Book not found");
        var book = found.get();
        command.updateBook(book);
        catalogRepository.save(book);
        return UpdateBookResponse.SUCCESS;
    }

    @Override
    public void removeById(Long id) {
        catalogRepository.removeById(id);
    }
}
