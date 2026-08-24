package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class BookService {
      
    @Autowired          
    private BookRepository bookRepository;    
                                                  
    public List<Book> getAllBooks(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByBookId(search, search);
        }                    
        return bookRepository.findAllByOrderByBookId(); 
    }              

    public void addBook(Book book) {
        book.setAvailable(book.getQuantity());
        bookRepository.save(book);
    }                          
                                    
    public Book updateBook(int bookId, Book book){
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {  
            return null;
        }                                         
        Book existing = optionalBook.get();  
        existing.setTitle(book.getTitle()); 
        existing.setAuthor(book.getAuthor());
        existing.setGenre(book.getGenre());    
                               
        int qtyDiff = book.getQuantity() - existing.getQuantity();
        existing.setQuantity(book.getQuantity());
        existing.setAvailable(Math.max(0, existing.getAvailable() + qtyDiff));

        return bookRepository.save(existing);
    } 
     
    public boolean deleteBook(int bookId) {
        if (!bookRepository.existsById(bookId)) {
            return false;
        }
        bookRepository.deleteById(bookId);
        return true;
    }
}
