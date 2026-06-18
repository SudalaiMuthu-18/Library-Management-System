package com.library.handler;

import com.library.dao.IssueDAO;
import com.library.model.IssuedBook;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles issue/return operations.
 *
 * POST /api/issue   → issue a book  { bookId, memberId }
 * POST /api/return  → return a book { issueId }
 * GET  /api/issued  → list all currently issued books
 */
public class IssueHandler extends BaseHandler implements HttpHandler {

    private final IssueDAO dao = new IssueDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (handlePreflight(ex)) return;

        String method = ex.getRequestMethod();
        String path   = ex.getRequestURI().getPath();

        try {
            if ("GET".equals(method) && path.startsWith("/api/issued")) {
                handleGetIssued(ex);
            } else if ("POST".equals(method) && path.startsWith("/api/issue")) {
                handleIssue(ex);
            } else if ("POST".equals(method) && path.startsWith("/api/return")) {
                handleReturn(ex);
            } else {
                sendError(ex, 405, "Method Not Allowed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(ex, 500, "Database error: " + e.getMessage());
        }
    }

    // ─── GET All Issued Books ─────────────────────────────────────────────────

    private void handleGetIssued(HttpExchange ex) throws IOException, SQLException {
        List<IssuedBook> list = dao.getAllIssuedBooks();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i).toJson());
        }
        sb.append("]");
        sendOk(ex, sb.toString());
    }

    // ─── POST Issue Book ──────────────────────────────────────────────────────

    private void handleIssue(HttpExchange ex) throws IOException, SQLException {
        String body     = readBody(ex);
        int    bookId   = JsonUtil.parseInt(body, "bookId");
        int    memberId = JsonUtil.parseInt(body, "memberId");

        if (bookId <= 0 || memberId <= 0) {
            sendError(ex, 400, "bookId and memberId are required.");
            return;
        }

        String result = dao.issueBook(bookId, memberId);
        switch (result) {
            case "SUCCESS":
                sendCreated(ex, JsonUtil.success("Book issued successfully!"));
                break;
            case "BOOK_NOT_FOUND":
                sendError(ex, 404, "Book not found.");
                break;
            case "MEMBER_NOT_FOUND":
                sendError(ex, 404, "Member not found.");
                break;
            case "NOT_AVAILABLE":
                sendError(ex, 409, "Book is not available (all copies are issued).");
                break;
            default:
                sendError(ex, 500, "Unknown error.");
        }
    }

    // ─── POST Return Book ─────────────────────────────────────────────────────

    private void handleReturn(HttpExchange ex) throws IOException, SQLException {
        String body    = readBody(ex);
        int    issueId = JsonUtil.parseInt(body, "issueId");

        if (issueId <= 0) {
            sendError(ex, 400, "issueId is required.");
            return;
        }

        String result = dao.returnBook(issueId);
        switch (result) {
            case "SUCCESS":
                sendOk(ex, JsonUtil.success("Book returned successfully!"));
                break;
            case "ISSUE_NOT_FOUND":
                sendError(ex, 404, "Issue record not found.");
                break;
            case "ALREADY_RETURNED":
                sendError(ex, 409, "This book has already been returned.");
                break;
            default:
                sendError(ex, 500, "Unknown error.");
        }
    }
}
