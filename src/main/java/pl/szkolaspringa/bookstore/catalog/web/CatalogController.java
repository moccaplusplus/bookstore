package pl.szkolaspringa.bookstore.catalog.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
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
import pl.szkolaspringa.bookstore.catalog.application.port.CatalogUseCase.FileInfo;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(@RequestParam(required = false) String author, @RequestParam(required = false) String title) {
        return catalogUseCase.findAllWithAuthors(title, author);
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return catalogUseCase.findOneWithAuthors(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> addBook(@Valid @RequestBody BookSaveDto dto) {
        var book = catalogUseCase.addBook(dto);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/" + book.getId().toString()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody BookSaveDto dto) {
        catalogUseCase.updateBook(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteById(@PathVariable Long id) {
        catalogUseCase.removeById(id);
    }

    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Got file:" + file.getOriginalFilename());
        var fileInfo = new FileInfo(file.getBytes(), file.getContentType(), file.getOriginalFilename());
        catalogUseCase.updateBookCover(id, fileInfo);
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeBookCover(@PathVariable Long id) {
        catalogUseCase.removeBookCover(id);
    }

    @Builder
    public record BookSaveDto(
            @NotBlank String title, @Singular @NotEmpty Set<Long> authors, @NotNull Integer year,
            @NotNull @DecimalMin("0.00") BigDecimal price, @NotNull @PositiveOrZero Long available) {
    }
}
