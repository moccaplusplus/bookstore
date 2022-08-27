package pl.szkolaspringa.bookstore.catalog.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;
    private String coverId;

    public Book(String title, String author, Integer year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }
}
