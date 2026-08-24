package com.library.service;

import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.IssuedBookRepository;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IssueService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IssuedBookRepository issuedBookRepository;

    public List<IssuedBook> getAllIssued() {
        return issuedBookRepository.findByStatusOrderByIssueIdDesc("ISSUED");
    }

    @Transactional                                  
    public String issueBook(Integer bookId, Integer memberId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            return "BOOK_NOT_FOUND";
        }
        Book book = optionalBook.get();
        if (book.getAvailable() <= 0) {
            return "NOT_AVAILABLE";
        }

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return "MEMBER_NOT_FOUND";
        }

        Member member = optionalMember.get();

        IssuedBook issuedBook = new IssuedBook();
        issuedBook.setBook(book);
        issuedBook.setMember(member);
        issuedBook.setStatus("ISSUED");
        issuedBook.setIssueDateRaw(LocalDateTime.now());
        issuedBook.setDueDateRaw(LocalDateTime.now().plusDays(14));

        book.setAvailable(book.getAvailable() - 1);
        bookRepository.save(book);
        issuedBookRepository.save(issuedBook);

        return "OK";
    }

    @Transactional
    public String returnBook(Integer issueId) {
        Optional<IssuedBook> optionalIssuedBook = issuedBookRepository.findById(issueId);
        if (optionalIssuedBook.isEmpty()) {
            return "ISSUE_NOT_FOUND";
        }

        IssuedBook issuedBook = optionalIssuedBook.get();
        if ("RETURNED".equals(issuedBook.getStatus())) {
            return "ALREADY_RETURNED";
        }
        
        issuedBook.setStatus("RETURNED");
        issuedBook.setReturnDateRaw(LocalDateTime.now());

        Book book = issuedBook.getBook();
        book.setAvailable(book.getAvailable() + 1);
        bookRepository.save(book);
        issuedBookRepository.save(issuedBook);
        return "OK";
    }
}
