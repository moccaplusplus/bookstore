package pl.szkolaspringa.bookstore.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.szkolaspringa.bookstore.BaseEntity;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Book extends BaseEntity<Long> {

    @Column(unique = true)
    private String title;

    @JsonIgnoreProperties("books")
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable
    @ToString.Exclude
    private Set<Author> authors = new HashSet<>();

    private Integer releaseYear;

    private BigDecimal price;

    @JsonProperty("coverId")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "cover_id", referencedColumnName = "id", nullable = true)
    private Upload cover;

    private Long available;

    public Book(String title, Integer releaseYear, BigDecimal price, Long available) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.price = price;
        this.available = available;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }

    public void clearAuthors() {
        authors.forEach(author -> author.getBooks().remove(this));
        authors.clear();
    }

    public void removeAvailable(int quantity) {
        if (quantity > available) {
            throw new IllegalArgumentException("Requested " + quantity + " of available " + available + " copies of book: " + getId() + ".");
        }
        available -= quantity;
    }

    public void addAvailable(int quantity) {
        available += quantity;
    }
}
