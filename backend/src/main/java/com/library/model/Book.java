package com.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKS")
public class Book {

    @Id   
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    @Column(name = "BOOK_ID")  
    private Integer bookId;
             
    @Column(name = "TITLE", nullable = false)
    private String title;
                                 
    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Column(name = "GENRE")
    private String genre = "General";

    @Column(name = "QUANTITY", nullable = false)
    private int quantity = 1;

    @Column(name = "AVAILABLE", nullable = false)
    private int available = 1;

    @Column(name = "ADDED_DATE", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime addedDate;

    // Constructors
    public Book() {}

    public Book(Integer bookId, String title, String author, String genre, int quantity, int available) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
        this.available = available;
    }

    // Getters and Setters
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public LocalDateTime getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
}
