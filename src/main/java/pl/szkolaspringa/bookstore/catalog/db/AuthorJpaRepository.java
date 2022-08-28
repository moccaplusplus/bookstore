package pl.szkolaspringa.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szkolaspringa.bookstore.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
}
