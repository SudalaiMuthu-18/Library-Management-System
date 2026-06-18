package com.library.dao;

import com.library.db.DBConnection;
import com.library.model.IssuedBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for book issue and return operations.
 */
public class IssueDAO {

    // ─── Issue a Book ─────────────────────────────────────────────────────────

    /**
     * Issues a book to a member.
     * Validates availability, inserts issue record, decrements available count.
     * @return status message
     */
    public String issueBook(int bookId, int memberId) throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // Check book availability
            String checkSql = "SELECT AVAILABLE FROM BOOKS WHERE BOOK_ID = ?";
            int available = 0;
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "BOOK_NOT_FOUND";
                    available = rs.getInt("AVAILABLE");
                }
            }
            if (available <= 0) return "NOT_AVAILABLE";

            // Check member exists
            String memSql = "SELECT COUNT(*) FROM MEMBERS WHERE MEMBER_ID = ?";
            try (PreparedStatement ps = con.prepareStatement(memSql)) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) return "MEMBER_NOT_FOUND";
                }
            }

            // Insert issue record (DUE_DATE = 14 days from now)
            String insertSql;
            if (con.getMetaData().getDatabaseProductName().contains("Oracle")) {
                insertSql = "INSERT INTO ISSUED_BOOKS (BOOK_ID, MEMBER_ID, DUE_DATE) " +
                            "VALUES (?, ?, SYSDATE + 14)";
            } else {
                insertSql = "INSERT INTO ISSUED_BOOKS (BOOK_ID, MEMBER_ID, DUE_DATE) " +
                            "VALUES (?, ?, datetime('now', '+14 days', 'localtime'))";
            }
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, bookId);
                ps.setInt(2, memberId);
                ps.executeUpdate();
            }

            // Decrement available
            String updateSql = "UPDATE BOOKS SET AVAILABLE = AVAILABLE - 1 WHERE BOOK_ID = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            con.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            if (con != null) try { con.close(); } catch (SQLException ignored) {}
        }
    }

    // ─── Return a Book ────────────────────────────────────────────────────────

    /**
     * Returns an issued book by its ISSUE_ID.
     * Updates the issue record and increments the available count.
     * @return status message
     */
    public String returnBook(int issueId) throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // Get the issue record
            String fetchSql = "SELECT BOOK_ID, STATUS FROM ISSUED_BOOKS WHERE ISSUE_ID = ?";
            int bookId = -1;
            try (PreparedStatement ps = con.prepareStatement(fetchSql)) {
                ps.setInt(1, issueId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "ISSUE_NOT_FOUND";
                    String status = rs.getString("STATUS");
                    if ("RETURNED".equals(status)) return "ALREADY_RETURNED";
                    bookId = rs.getInt("BOOK_ID");
                }
            }

            // Mark as returned
            String updateIssueSql;
            if (con.getMetaData().getDatabaseProductName().contains("Oracle")) {
                updateIssueSql = "UPDATE ISSUED_BOOKS SET STATUS='RETURNED', " +
                                 "RETURN_DATE=SYSDATE WHERE ISSUE_ID=?";
            } else {
                updateIssueSql = "UPDATE ISSUED_BOOKS SET STATUS='RETURNED', " +
                                 "RETURN_DATE=datetime('now', 'localtime') WHERE ISSUE_ID=?";
            }
            try (PreparedStatement ps = con.prepareStatement(updateIssueSql)) {
                ps.setInt(1, issueId);
                ps.executeUpdate();
            }

            // Increment available
            String updateBookSql = "UPDATE BOOKS SET AVAILABLE = AVAILABLE + 1 WHERE BOOK_ID = ?";
            try (PreparedStatement ps = con.prepareStatement(updateBookSql)) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
            }

            con.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            if (con != null) try { con.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            if (con != null) try { con.close(); } catch (SQLException ignored) {}
        }
    }

    // ─── Get All Currently Issued Books ───────────────────────────────────────

    public List<IssuedBook> getAllIssuedBooks() throws SQLException {
        List<IssuedBook> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            String sql;
            if (con.getMetaData().getDatabaseProductName().contains("Oracle")) {
                sql = "SELECT i.ISSUE_ID, i.BOOK_ID, b.TITLE, i.MEMBER_ID, m.NAME, " +
                      "       TO_CHAR(i.ISSUE_DATE,'YYYY-MM-DD'), " +
                      "       TO_CHAR(i.DUE_DATE,'YYYY-MM-DD'), " +
                      "       TO_CHAR(i.RETURN_DATE,'YYYY-MM-DD'), " +
                      "       i.STATUS " +
                      "FROM ISSUED_BOOKS i " +
                      "JOIN BOOKS b   ON i.BOOK_ID   = b.BOOK_ID " +
                      "JOIN MEMBERS m ON i.MEMBER_ID = m.MEMBER_ID " +
                      "WHERE i.STATUS = 'ISSUED' " +
                      "ORDER BY i.ISSUE_DATE DESC";
            } else {
                sql = "SELECT i.ISSUE_ID, i.BOOK_ID, b.TITLE, i.MEMBER_ID, m.NAME, " +
                      "       i.ISSUE_DATE, " +
                      "       i.DUE_DATE, " +
                      "       i.RETURN_DATE, " +
                      "       i.STATUS " +
                      "FROM ISSUED_BOOKS i " +
                      "JOIN BOOKS b   ON i.BOOK_ID   = b.BOOK_ID " +
                      "JOIN MEMBERS m ON i.MEMBER_ID = m.MEMBER_ID " +
                      "WHERE i.STATUS = 'ISSUED' " +
                      "ORDER BY i.ISSUE_DATE DESC";
            }
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new IssuedBook(
                        rs.getInt(1), rs.getInt(2), rs.getString(3),
                        rs.getInt(4), rs.getString(5),
                        rs.getString(6), rs.getString(7),
                        rs.getString(8), rs.getString(9)
                    ));
                }
            }
        }
        return list;
    }

    // ─── Dashboard Stats ──────────────────────────────────────────────────────

    public int getIssuedCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ISSUED_BOOKS WHERE STATUS = 'ISSUED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
