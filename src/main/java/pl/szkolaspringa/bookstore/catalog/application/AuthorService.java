package pl.szkolaspringa.bookstore.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.catalog.application.port.AuthorUseCase;
import pl.szkolaspringa.bookstore.catalog.db.AuthorJpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService implements AuthorUseCase {

    private final AuthorJpaRepository authorJpaRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Author> findAll() {
        return authorJpaRepository.findAll();
    }

    @Override
    public List<Author> findAllEager() {
        return authorJpaRepository.findAllEager();
    }
}
