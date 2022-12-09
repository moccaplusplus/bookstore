package pl.szkolaspringa.bookstore.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.szkolaspringa.bookstore.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Author extends BaseEntity<Long> {

    public static Author of(String name) {
        var p = name.lastIndexOf(" ");
        return p > 0 ? new Author(name.substring(0, p).trim(), name.substring(p + 1).trim()) :
                new Author(null, name);
    }

    private String firstName;

    private String lastName;

    @ManyToMany(cascade = {MERGE, PERSIST}, mappedBy = "authors")
    @JsonIgnoreProperties({"authors"})
    @ToString.Exclude
    private Set<Book> books = new HashSet<>();

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addBook(Book book) {
        books.add(book);
        book.getAuthors().add(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.getAuthors().remove(this);
    }
}
