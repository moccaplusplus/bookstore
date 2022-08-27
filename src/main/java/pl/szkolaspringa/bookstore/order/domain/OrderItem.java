package pl.szkolaspringa.bookstore.order.domain;

import pl.szkolaspringa.bookstore.catalog.domain.Book;

public record OrderItem(Book book, int quantity) {
}
