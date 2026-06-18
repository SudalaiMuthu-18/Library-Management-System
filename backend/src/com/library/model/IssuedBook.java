package com.library.model;

/**
 * Represents an issued book record (join of ISSUED_BOOKS, BOOKS, MEMBERS).
 */
public class IssuedBook {

    private int    issueId;
    private int    bookId;
    private String bookTitle;
    private int    memberId;
    private String memberName;
    private String issueDate;
    private String dueDate;
    private String returnDate;
    private String status;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public IssuedBook() {}

    public IssuedBook(int issueId, int bookId, String bookTitle,
                      int memberId, String memberName,
                      String issueDate, String dueDate,
                      String returnDate, String status) {
        this.issueId    = issueId;
        this.bookId     = bookId;
        this.bookTitle  = bookTitle;
        this.memberId   = memberId;
        this.memberName = memberName;
        this.issueDate  = issueDate;
        this.dueDate    = dueDate;
        this.returnDate = returnDate;
        this.status     = status;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public int    getIssueId()    { return issueId;    }
    public int    getBookId()     { return bookId;     }
    public String getBookTitle()  { return bookTitle;  }
    public int    getMemberId()   { return memberId;   }
    public String getMemberName() { return memberName; }
    public String getIssueDate()  { return issueDate;  }
    public String getDueDate()    { return dueDate;    }
    public String getReturnDate() { return returnDate; }
    public String getStatus()     { return status;     }

    public void setIssueId(int v)      { issueId    = v; }
    public void setBookId(int v)       { bookId     = v; }
    public void setBookTitle(String v) { bookTitle  = v; }
    public void setMemberId(int v)     { memberId   = v; }
    public void setMemberName(String v){ memberName = v; }
    public void setIssueDate(String v) { issueDate  = v; }
    public void setDueDate(String v)   { dueDate    = v; }
    public void setReturnDate(String v){ returnDate = v; }
    public void setStatus(String v)    { status     = v; }

    // ─── JSON Serialization ───────────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"issueId\":%d,\"bookId\":%d,\"bookTitle\":\"%s\"," +
            "\"memberId\":%d,\"memberName\":\"%s\"," +
            "\"issueDate\":\"%s\",\"dueDate\":\"%s\"," +
            "\"returnDate\":\"%s\",\"status\":\"%s\"}",
            issueId, bookId, esc(bookTitle),
            memberId, esc(memberName),
            esc(issueDate), esc(dueDate),
            esc(returnDate == null ? "" : returnDate),
            esc(status)
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
