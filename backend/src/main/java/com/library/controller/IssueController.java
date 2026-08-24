package com.library.controller;

import com.library.model.IssuedBook;
import com.library.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @GetMapping("/issued")
    public ResponseEntity<List<IssuedBook>> getAllIssued() {
        try {     
            return ResponseEntity.ok(issueService.getAllIssued());
        } catch (Exception e) {   
            return ResponseEntity.status(500).build();
        }  
    }           

    @PostMapping("/issue")
    public ResponseEntity<Map<String, String>> issueBook(@RequestBody Map<String, Integer> payload) {
        Integer bookId = payload.get("bookId");
        Integer memberId = payload.get("memberId");

        if (bookId == null || memberId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Book ID and Member ID are required."));
        }   

        try {
            String result = issueService.issueBook(bookId, memberId);
            return switch (result) {
                case "OK"               -> ResponseEntity.ok(Map.of("message", "Book issued successfully!"));
                case "BOOK_NOT_FOUND"   -> ResponseEntity.badRequest().body(Map.of("error", "BOOK_NOT_FOUND"));
                case "NOT_AVAILABLE"    -> ResponseEntity.badRequest().body(Map.of("error", "NOT_AVAILABLE"));
                case "MEMBER_NOT_FOUND" -> ResponseEntity.badRequest().body(Map.of("error", "MEMBER_NOT_FOUND"));
                default                 -> ResponseEntity.status(500).body(Map.of("error", "Unexpected error."));
            };
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Map<String, String>> returnBook(@RequestBody Map<String, Integer> payload) {
        Integer issueId = payload.get("issueId");

        if (issueId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Issue ID is required."));
        }

        try {
            String result = issueService.returnBook(issueId);
            return switch (result) {
                case "OK"               -> ResponseEntity.ok(Map.of("message", "Book returned successfully!"));
                case "ISSUE_NOT_FOUND"  -> ResponseEntity.badRequest().body(Map.of("error", "ISSUE_NOT_FOUND"));
                case "ALREADY_RETURNED" -> ResponseEntity.badRequest().body(Map.of("error", "ALREADY_RETURNED"));
                default                 -> ResponseEntity.status(500).body(Map.of("error", "Unexpected error."));
            };
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
