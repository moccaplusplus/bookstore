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
    Optional<Book> findOneByTitle(@Param("title") String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
            "lower(b.title) LIKE lower(concat('%', :title, '%'))" +
            "AND " +
            "lower(concat(a.firstName, ' ', a.lastName)) LIKE lower(concat('%', :author, '%'))")
    Optional<Book> findOneByTitleAndAuthor(@Param("title") String title, @Param("author") String author);

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors a WHERE " +
            "( :title IS NULL OR lower(b.title) LIKE lower(concat('%', :title, '%')) )" +
            "AND " +
            "( :author IS NULL OR lower(concat(a.firstName, ' ', a.lastName)) LIKE lower(concat('%', :author, '%')) )")
    List<Book> findAllWithAuthors(@Param("title") String title, @Param("author") String author);

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors a WHERE b.id=:id")
    Optional<Book> findOneWithAuthors(@Param("id") Long id);
}
