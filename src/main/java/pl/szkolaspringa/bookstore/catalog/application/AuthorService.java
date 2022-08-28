package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.catalog.application.port.AuthorUseCase;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService implements AuthorUseCase {

    private final AuthorJpaRepository authorJpaRepository;

    @Override
    public List<Author> findAll() {
        return authorJpaRepository.findAll();
    }
}
