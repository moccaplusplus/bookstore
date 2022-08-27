package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.domain.Book;
import pl.szkolaspringa.bookstore.catalog.domain.CatalogRepository;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase.SaveUploadCommand;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CatalogService implements CatalogUseCase {

    private final CatalogRepository catalogRepository;
    private final UploadUseCase uploadUseCase;

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Override
    public List<Book> findAllByAuthor(String author) {
        return catalogRepository.findAll().stream()
                .filter(book -> startsWithLowerCase(book.getAuthor(), author))
                .collect(toList());
    }

    @Override
    public List<Book> findAllByTitle(String title) {
        return catalogRepository.findAll().stream()
                .filter(book -> startsWithLowerCase(book.getTitle(), title))
                .collect(toList());
    }

    @Override
    public List<Book> findAllByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll().stream()
                .filter(book -> startsWithLowerCase(book.getTitle(), title))
                .filter(book -> startsWithLowerCase(book.getAuthor(), author))
                .collect(toList());
    }

    @Override
    public Optional<Book> findOneById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return catalogRepository.findAll().stream()
                .filter(book -> startsWithLowerCase(book.getTitle(), title))
                .findAny();
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll().stream()
                .filter(book -> startsWithLowerCase(book.getTitle(), title))
                .filter(book -> startsWithLowerCase(book.getAuthor(), author))
                .findAny();
    }

    @Override
    public Book addBook(AddBookCommand command) {
        var book = new Book(command.title(), command.author(), command.year(), command.price());
        return catalogRepository.save(book);
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

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        catalogRepository.findById(command.id()).ifPresent(book -> {
            var cmd = new SaveUploadCommand(command.fileName(), command.file(), command.contentType());
            var saved = uploadUseCase.save(cmd);
            book.setCoverId(saved.id());
            catalogRepository.save(book);
        });
    }

    @Override
    public void removeBookCover(Long id) {
        catalogRepository.findById(id).ifPresent(book -> {
            Optional.ofNullable(book.getCoverId()).ifPresent(coverId -> {
                uploadUseCase.removeById(coverId);
                book.setCoverId(null);
                catalogRepository.save(book);
            });

        });
    }

    private static boolean startsWithLowerCase(String text, String prefix) {
        return text.toLowerCase().startsWith(prefix.toLowerCase());
    }
}
