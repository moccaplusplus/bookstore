package pl.szkolaspringa.bookstore.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.szkolaspringa.bookstore.BaseEntity;
import pl.szkolaspringa.bookstore.catalog.domain.Book;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "purchase_item")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity<Long> {

    @ManyToOne
    private Book book;

    private int quantity;
}
