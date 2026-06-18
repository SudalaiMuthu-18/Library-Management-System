package com.library.model;

/**
 * Represents a book in the library.
 */
public class Book {

    private int    bookId;
    private String title;
    private String author;
    private String genre;
    private int    quantity;
    private int    available;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Book() {}

    public Book(int bookId, String title, String author, String genre, int quantity, int available) {
        this.bookId    = bookId;
        this.title     = title;
        this.author    = author;
        this.genre     = genre;
        this.quantity  = quantity;
        this.available = available;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public int    getBookId()    { return bookId;    }
    public String getTitle()     { return title;     }
    public String getAuthor()    { return author;    }
    public String getGenre()     { return genre;     }
    public int    getQuantity()  { return quantity;  }
    public int    getAvailable() { return available; }

    public void setBookId(int bookId)       { this.bookId    = bookId;    }
    public void setTitle(String title)      { this.title     = title;     }
    public void setAuthor(String author)    { this.author    = author;    }
    public void setGenre(String genre)      { this.genre     = genre;     }
    public void setQuantity(int quantity)   { this.quantity  = quantity;  }
    public void setAvailable(int available) { this.available = available; }

    // ─── JSON Serialization ───────────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"bookId\":%d,\"title\":\"%s\",\"author\":\"%s\",\"genre\":\"%s\",\"quantity\":%d,\"available\":%d}",
            bookId,
            esc(title),
            esc(author),
            esc(genre),
            quantity,
            available
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
