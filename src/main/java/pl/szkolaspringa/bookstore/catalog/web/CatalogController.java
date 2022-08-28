package pl.szkolaspringa.bookstore.catalog.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(@RequestParam Optional<String> author, @RequestParam Optional<String> title) {
        if (author.isPresent()) {
            if (title.isPresent()) {
                return catalogUseCase.findByTitleAndAuthor(title.get(), author.get());
            } else {
                return catalogUseCase.findByAuthor(author.get());
            }
        } else if (title.isPresent()) {
            return catalogUseCase.findByTitle(title.get());
        }
        return catalogUseCase.findAll();
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return catalogUseCase.findOneById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Void> addBook(@Valid @RequestBody BookDto dto) {
        var command = new CatalogUseCase.AddBookCommand(dto.title(), dto.authors(), dto.year(), dto.price());
        var book = catalogUseCase.addBook(command);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + book.getId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody BookDto dto) {
        var command = new CatalogUseCase.UpdateBookCommand(id, dto.title(), dto.authors(), dto.year(), dto.price());
        var result = catalogUseCase.updateBook(command);
        if (!result.success()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", result.errors()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteById(@PathVariable Long id) {
        catalogUseCase.removeById(id);
    }

    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Got file:" + file.getOriginalFilename());
        var command = new CatalogUseCase.UpdateBookCoverCommand(id, file.getBytes(), file.getContentType(), file.getOriginalFilename());
        catalogUseCase.updateBookCover(command);
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeBookCover(@PathVariable Long id) {
        catalogUseCase.removeBookCover(id);
    }

    public record BookDto(
            @NotBlank String title, @NotEmpty Set<Long> authors, @NotNull Integer year,
            @NotNull @DecimalMin("0.00") BigDecimal price) {
    }
}
