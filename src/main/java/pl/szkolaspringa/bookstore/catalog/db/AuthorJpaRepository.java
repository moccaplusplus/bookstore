package pl.szkolaspringa.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.szkolaspringa.bookstore.catalog.domain.Author;

import java.util.List;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a JOIN FETCH a.books b")
    List<Author> findAllEager();
}
