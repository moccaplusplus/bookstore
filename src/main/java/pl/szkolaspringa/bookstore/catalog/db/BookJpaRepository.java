package pl.szkolaspringa.bookstore.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE lower(b.title) LIKE lower(concat('%', :title, '%'))")
    List<Book> findByTitle(@Param("title") String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
            "lower(concat(a.firstName, ' ', a.lastName)) LIKE lower(concat('%', :author, '%'))")
    List<Book> findByAuthor(@Param("author") String author);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
            "lower(b.title) LIKE lower(concat('%', :title, '%'))" +
            "AND " +
            "lower(concat(a.firstName, ' ', a.lastName)) LIKE lower(concat('%', :author, '%'))")
    List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);

    @Query("SELECT b FROM Book b WHERE lower(b.title) LIKE lower(concat('%', :title, '%'))")
    Optional<Book> findOneByTitle(String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
            "lower(b.title) LIKE lower(concat('%', :title, '%'))" +
            "AND " +
            "lower(concat(a.firstName, ' ', a.lastName)) LIKE lower(concat('%', :author, '%'))")
    Optional<Book> findOneByTitleAndAuthor(String title, String author);
}
