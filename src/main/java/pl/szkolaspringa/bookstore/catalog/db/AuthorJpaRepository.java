package pl.szkolaspringa.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.szkolaspringa.bookstore.catalog.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

    @Query("SELECT DISTINCT a FROM Author a JOIN FETCH a.books b")
    List<Author> findAllEager();

    @Query("SELECT DISTINCT a FROM Author a WHERE " +
            "lower(concat(a.firstName, ' ', a.lastName)) = lower(:author)")
    Optional<Author> findByNameIgnoreCase(@Param("author") String author);
}
