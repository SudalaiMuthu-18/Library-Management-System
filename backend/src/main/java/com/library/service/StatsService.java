package com.library.service;

import com.library.repository.BookRepository;
import com.library.repository.IssuedBookRepository;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatsService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IssuedBookRepository issuedBookRepository;

    public Map<String, Object> getStats() {
        int totalBooks = bookRepository.sumQuantity();
        int availableBooks = bookRepository.sumAvailable();
        long totalMembers = memberRepository.count();
        int issuedBooks = issuedBookRepository.countByStatus("ISSUED");

        return Map.of(
            "totalBooks", totalBooks,
            "availableBooks", availableBooks,
            "totalMembers", totalMembers,
            "issuedBooks", issuedBooks
        );
    }
}
