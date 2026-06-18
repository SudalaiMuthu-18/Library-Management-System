package com.library.dao;

import com.library.db.DBConnection;
import com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the BOOKS table.
 */
public class BookDAO {

    // ─── Get All Books ────────────────────────────────────────────────────────

    public List<Book> getAllBooks() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT BOOK_ID, TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE " +
                     "FROM BOOKS ORDER BY BOOK_ID";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ─── Search Books ─────────────────────────────────────────────────────────

    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT BOOK_ID, TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE FROM BOOKS " +
                     "WHERE UPPER(TITLE) LIKE UPPER(?) OR UPPER(AUTHOR) LIKE UPPER(?) " +
                     "ORDER BY BOOK_ID";
        String kw = "%" + keyword + "%";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ─── Get Book by ID ───────────────────────────────────────────────────────

    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT BOOK_ID, TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE " +
                     "FROM BOOKS WHERE BOOK_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ─── Add Book ─────────────────────────────────────────────────────────────

    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre() != null ? book.getGenre() : "General");
            ps.setInt(4, book.getQuantity() > 0 ? book.getQuantity() : 1);
            ps.setInt(5, book.getQuantity() > 0 ? book.getQuantity() : 1);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Update Book ──────────────────────────────────────────────────────────

    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE BOOKS SET TITLE=?, AUTHOR=?, GENRE=?, QUANTITY=?, AVAILABLE=? " +
                     "WHERE BOOK_ID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre() != null ? book.getGenre() : "General");
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getAvailable());
            ps.setInt(6, book.getBookId());
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Delete Book ──────────────────────────────────────────────────────────

    public boolean deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM BOOKS WHERE BOOK_ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Count Books ──────────────────────────────────────────────────────────

    public int[] getCounts() throws SQLException {
        String sql = "SELECT SUM(QUANTITY), SUM(AVAILABLE) FROM BOOKS";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new int[]{ rs.getInt(1), rs.getInt(2) };
            }
        }
        return new int[]{ 0, 0 };
    }

    // ─── Row mapper ───────────────────────────────────────────────────────────

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("BOOK_ID"),
            rs.getString("TITLE"),
            rs.getString("AUTHOR"),
            rs.getString("GENRE"),
            rs.getInt("QUANTITY"),
            rs.getInt("AVAILABLE")
        );
    }
}
