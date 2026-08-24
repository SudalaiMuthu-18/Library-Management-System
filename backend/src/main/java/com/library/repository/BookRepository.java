package com.library.repository;

import com.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByBookId(String title, String author);

    List<Book> findAllByOrderByBookId(); 

    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM Book b")
    int sumQuantity();

    @Query("SELECT COALESCE(SUM(b.available), 0) FROM Book b")
    int sumAvailable();
        
}
