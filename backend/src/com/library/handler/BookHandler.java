package com.library.handler;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles all /api/books requests.
 *
 * GET    /api/books           → list all (optional ?search=keyword)
 * POST   /api/books           → add book
 * PUT    /api/books/{id}      → update book
 * DELETE /api/books/{id}      → delete book
 */
public class BookHandler extends BaseHandler implements HttpHandler {

    private final BookDAO dao = new BookDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (handlePreflight(ex)) return;

        String method = ex.getRequestMethod();
        String path   = ex.getRequestURI().getPath();

        try {
            // /api/books  or  /api/books/
            boolean isCollection = path.matches("^/api/books/?$");

            if ("GET".equals(method) && isCollection) {
                handleGetAll(ex);
            } else if ("POST".equals(method) && isCollection) {
                handleAdd(ex);
            } else if ("PUT".equals(method) && !isCollection) {
                handleUpdate(ex);
            } else if ("DELETE".equals(method) && !isCollection) {
                handleDelete(ex);
            } else {
                sendError(ex, 405, "Method Not Allowed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(ex, 500, "Database error: " + e.getMessage());
        }
    }

    // ─── GET All / Search ─────────────────────────────────────────────────────

    private void handleGetAll(HttpExchange ex) throws IOException, SQLException {
        String search = parseParam(ex, "search");
        List<Book> books = (search != null && !search.isEmpty())
            ? dao.searchBooks(search)
            : dao.getAllBooks();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < books.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(books.get(i).toJson());
        }
        sb.append("]");
        sendOk(ex, sb.toString());
    }

    // ─── POST Add ─────────────────────────────────────────────────────────────

    private void handleAdd(HttpExchange ex) throws IOException, SQLException {
        String body = readBody(ex);
        String title    = JsonUtil.parse(body, "title");
        String author   = JsonUtil.parse(body, "author");
        String genre    = JsonUtil.parse(body, "genre");
        int    quantity = JsonUtil.parseInt(body, "quantity");

        if (title == null || title.isEmpty() || author == null || author.isEmpty()) {
            sendError(ex, 400, "Title and Author are required.");
            return;
        }
        if (quantity <= 0) quantity = 1;

        Book book = new Book(0, title, author,
                             (genre == null || genre.isEmpty()) ? "General" : genre,
                             quantity, quantity);
        boolean ok = dao.addBook(book);
        if (ok) sendCreated(ex, JsonUtil.success("Book added successfully."));
        else    sendError(ex, 500, "Failed to add book.");
    }

    // ─── PUT Update ───────────────────────────────────────────────────────────

    private void handleUpdate(HttpExchange ex) throws IOException, SQLException {
        int id = extractId(ex);
        if (id == -1) { sendError(ex, 400, "Invalid book ID."); return; }

        String body = readBody(ex);
        String title     = JsonUtil.parse(body, "title");
        String author    = JsonUtil.parse(body, "author");
        String genre     = JsonUtil.parse(body, "genre");
        int    quantity  = JsonUtil.parseInt(body, "quantity");
        int    available = JsonUtil.parseInt(body, "available");

        if (title == null || title.isEmpty() || author == null || author.isEmpty()) {
            sendError(ex, 400, "Title and Author are required.");
            return;
        }

        Book book = new Book(id, title, author,
                             (genre == null || genre.isEmpty()) ? "General" : genre,
                             quantity, available);
        boolean ok = dao.updateBook(book);
        if (ok) sendOk(ex, JsonUtil.success("Book updated successfully."));
        else    sendError(ex, 404, "Book not found.");
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    private void handleDelete(HttpExchange ex) throws IOException, SQLException {
        int id = extractId(ex);
        if (id == -1) { sendError(ex, 400, "Invalid book ID."); return; }

        boolean ok = dao.deleteBook(id);
        if (ok) sendOk(ex, JsonUtil.success("Book deleted successfully."));
        else    sendError(ex, 404, "Book not found.");
    }
}
