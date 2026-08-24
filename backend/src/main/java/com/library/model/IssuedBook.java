package com.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ISSUED_BOOKS")
public class IssuedBook {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ISSUE_ID")
    private Integer issueId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    private Book book;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Column(name = "ISSUE_DATE")
    private LocalDateTime issueDate;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "RETURN_DATE")
    private LocalDateTime returnDate;

    @Column(name = "STATUS")
    private String status = "ISSUED";

    @PrePersist
    protected void onCreate() {
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
        if (dueDate == null) {
            dueDate = issueDate.plusDays(14);
        }
    }

    // Constructors
    public IssuedBook() {}

    // Getters and Setters
    public Integer getIssueId() { return issueId; }
    public void setIssueId(Integer issueId) { this.issueId = issueId; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public LocalDateTime getIssueDateRaw() { return issueDate; }
    public void setIssueDateRaw(LocalDateTime issueDate) { this.issueDate = issueDate; }
    public LocalDateTime getDueDateRaw() { return dueDate; }
    public void setDueDateRaw(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getReturnDateRaw() { return returnDate; }
    public void setReturnDateRaw(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // JSON Getters matching old format
    public int getBookId() {
        return book != null ? book.getBookId() : 0;
    }

    public String getBookTitle() {
        return book != null ? book.getTitle() : "";
    }

    public int getMemberId() {
        return member != null ? member.getMemberId() : 0;
    }

    public String getMemberName() {
        return member != null ? member.getName() : "";
    }

    @Transient
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String getIssueDate() {
        return issueDate != null ? issueDate.format(FORMATTER) : "";
    }

    public String getDueDate() {
        return dueDate != null ? dueDate.format(FORMATTER) : "";
    }

    public String getReturnDate() {
        return returnDate != null ? returnDate.format(FORMATTER) : "";
    }
}
