package com.library.controller;
    
import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(bookService.getAllBooks(search));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addBook(@RequestBody Book book) {
        try {
            bookService.addBook(book);
            return ResponseEntity.ok(Map.of("message", "Book added successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> updateBook(@PathVariable int bookId, @RequestBody Book book) {
        try {
            Book updated = bookService.updateBook(bookId, book);
            if (updated == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Book not found."));
            }
            return ResponseEntity.ok(Map.of("message", "Book updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable int bookId) {
        try {
            if (!bookService.deleteBook(bookId)) {
                return ResponseEntity.status(404).body(Map.of("error", "Book not found."));
            }
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
