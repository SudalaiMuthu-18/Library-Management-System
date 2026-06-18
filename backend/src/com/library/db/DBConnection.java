package com.library.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Resilient Database Connection Manager.
 * Tries connecting to Oracle DB first. If the Oracle DB listener is stopped 
 * or not running (e.g. ORA-12541), it automatically falls back to an 
 * embedded local SQLite database and initializes the tables and sample data.
 */
public class DBConnection {

    private static final String URL      = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:oracle:thin:@localhost:1521:ORCL";
    private static final String USERNAME = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "system";
    private static final String PASSWORD = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : "oracle";

    private static boolean useSQLiteFallback = false;

    static {
        // Load Oracle Driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("[DB] Oracle JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB WARNING] Oracle JDBC Driver not found in lib/ folder.");
        }
        
        // Load SQLite Driver
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("[DB] SQLite JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB WARNING] SQLite JDBC Driver not found in lib/ folder.");
        }
    }

    /**
     * Obtains a connection, dynamically falling back to SQLite if Oracle fails.
     */
    public static Connection getConnection() throws SQLException {
        if (useSQLiteFallback) {
            return getSQLiteConnection();
        }

        try {
            // Try connecting to Oracle
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            // Check for Listener connection error (ORA-12541) or missing service
            if (e.getErrorCode() == 12541 || e.getMessage().contains("ORA-12541") || e.getMessage().contains("No listener")) {
                System.out.println("[DB WARNING] Oracle Listener not running at " + URL);
                System.out.println("             Auto-switching to Embedded SQLite Database (library.db)...");
                useSQLiteFallback = true;
                return getSQLiteConnection();
            } else {
                // If it is some other error, log it but still try to switch
                System.out.println("[DB WARNING] Oracle connection failed: " + e.getMessage());
                System.out.println("             Auto-switching to Embedded SQLite Database (library.db)...");
                useSQLiteFallback = true;
                return getSQLiteConnection();
            }
        }
    }

    private static Connection getSQLiteConnection() throws SQLException {
        File dbFile = new File("library.db");
        boolean exists = dbFile.exists() && dbFile.length() > 0;
        
        Connection con = DriverManager.getConnection("jdbc:sqlite:library.db");
        if (!exists) {
            initializeSQLiteSchema(con);
        }
        return con;
    }

    private static void initializeSQLiteSchema(Connection con) {
        System.out.println("[DB INFO] First-time setup: Initializing SQLite schema...");
        try (Statement stmt = con.createStatement()) {
            // Create Books Table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS BOOKS (" +
                "  BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  TITLE TEXT NOT NULL," +
                "  AUTHOR TEXT NOT NULL," +
                "  GENRE TEXT DEFAULT 'General'," +
                "  QUANTITY INTEGER DEFAULT 1 NOT NULL," +
                "  AVAILABLE INTEGER DEFAULT 1 NOT NULL," +
                "  ADDED_DATE DATE DEFAULT (datetime('now', 'localtime'))" +
                ")"
            );

            // Create Members Table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS MEMBERS (" +
                "  MEMBER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  NAME TEXT NOT NULL," +
                "  EMAIL TEXT," +
                "  PHONE TEXT," +
                "  JOINED_DATE DATE DEFAULT (datetime('now', 'localtime'))" +
                ")"
            );

            // Create Issued Books Table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS ISSUED_BOOKS (" +
                "  ISSUE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  BOOK_ID INTEGER NOT NULL REFERENCES BOOKS(BOOK_ID) ON DELETE CASCADE," +
                "  MEMBER_ID INTEGER NOT NULL REFERENCES MEMBERS(MEMBER_ID) ON DELETE CASCADE," +
                "  ISSUE_DATE TEXT DEFAULT (datetime('now', 'localtime'))," +
                "  DUE_DATE TEXT DEFAULT (datetime('now', '+14 days', 'localtime'))," +
                "  RETURN_DATE TEXT," +
                "  STATUS TEXT DEFAULT 'ISSUED'" +
                ")"
            );

            // Insert Mock Books
            stmt.execute("INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE) VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', 3, 3)");
            stmt.execute("INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE) VALUES ('To Kill a Mockingbird', 'Harper Lee', 'Classic', 2, 2)");
            stmt.execute("INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE) VALUES ('1984', 'George Orwell', 'Dystopian', 4, 4)");
            stmt.execute("INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE) VALUES ('Harry Potter', 'J.K. Rowling', 'Fantasy', 5, 5)");
            stmt.execute("INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE) VALUES ('The Alchemist', 'Paulo Coelho', 'Fiction', 3, 3)");

            // Insert Mock Members
            stmt.execute("INSERT INTO MEMBERS (NAME, EMAIL, PHONE) VALUES ('Alice Johnson', 'alice@example.com', '9876543210')");
            stmt.execute("INSERT INTO MEMBERS (NAME, EMAIL, PHONE) VALUES ('Bob Smith', 'bob@example.com', '9123456789')");
            stmt.execute("INSERT INTO MEMBERS (NAME, EMAIL, PHONE) VALUES ('Carol White', 'carol@example.com', '9988776655')");

            System.out.println("[DB INFO] Embedded SQLite Database fully initialized!");
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to initialize SQLite schema: " + e.getMessage());
        }
    }
}
