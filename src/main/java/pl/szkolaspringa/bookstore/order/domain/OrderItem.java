package pl.szkolaspringa.bookstore.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.szkolaspringa.bookstore.BaseEntity;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "purchase_item")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Positive
    private int quantity;
}
