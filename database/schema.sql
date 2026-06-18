-- ============================================================
--   LIBRARY MANAGEMENT SYSTEM — Oracle Database Schema
--   Run this script in SQL*Plus or SQL Developer
-- ============================================================

-- Drop tables if they exist (clean slate)
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE ISSUED_BOOKS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE BOOKS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE MEMBERS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE BOOK_SEQ';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE MEMBER_SEQ';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- ============================================================
--  Sequences for auto-increment IDs
-- ============================================================
CREATE SEQUENCE BOOK_SEQ   START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE MEMBER_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- ============================================================
--  BOOKS Table
-- ============================================================
CREATE TABLE BOOKS (
    BOOK_ID    NUMBER         DEFAULT BOOK_SEQ.NEXTVAL PRIMARY KEY,
    TITLE      VARCHAR2(200)  NOT NULL,
    AUTHOR     VARCHAR2(100)  NOT NULL,
    GENRE      VARCHAR2(50)   DEFAULT 'General',
    QUANTITY   NUMBER         DEFAULT 1 NOT NULL,
    AVAILABLE  NUMBER         DEFAULT 1 NOT NULL,
    ADDED_DATE DATE           DEFAULT SYSDATE
);

-- ============================================================
--  MEMBERS Table
-- ============================================================
CREATE TABLE MEMBERS (
    MEMBER_ID   NUMBER        DEFAULT MEMBER_SEQ.NEXTVAL PRIMARY KEY,
    NAME        VARCHAR2(100) NOT NULL,
    EMAIL       VARCHAR2(100),
    PHONE       VARCHAR2(15),
    JOINED_DATE DATE          DEFAULT SYSDATE
);

-- ============================================================
--  ISSUED_BOOKS Table
-- ============================================================
CREATE TABLE ISSUED_BOOKS (
    ISSUE_ID    NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    BOOK_ID     NUMBER        NOT NULL REFERENCES BOOKS(BOOK_ID),
    MEMBER_ID   NUMBER        NOT NULL REFERENCES MEMBERS(MEMBER_ID),
    ISSUE_DATE  DATE          DEFAULT SYSDATE NOT NULL,
    DUE_DATE    DATE          DEFAULT SYSDATE + 14,
    RETURN_DATE DATE,
    STATUS      VARCHAR2(10)  DEFAULT 'ISSUED'
);

-- ============================================================
--  Sample Data
-- ============================================================
INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE)
VALUES ('The Great Gatsby',        'F. Scott Fitzgerald', 'Classic',  3, 3);

INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE)
VALUES ('To Kill a Mockingbird',   'Harper Lee',          'Classic',  2, 2);

INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE)
VALUES ('1984',                    'George Orwell',       'Dystopian',4, 4);

INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE)
VALUES ('Harry Potter and the Philosophers Stone', 'J.K. Rowling', 'Fantasy', 5, 5);

INSERT INTO BOOKS (TITLE, AUTHOR, GENRE, QUANTITY, AVAILABLE)
VALUES ('The Alchemist',           'Paulo Coelho',        'Fiction',  3, 3);

INSERT INTO MEMBERS (NAME, EMAIL, PHONE)
VALUES ('Alice Johnson', 'alice@example.com', '9876543210');

INSERT INTO MEMBERS (NAME, EMAIL, PHONE)
VALUES ('Bob Smith',     'bob@example.com',   '9123456789');

INSERT INTO MEMBERS (NAME, EMAIL, PHONE)
VALUES ('Carol White',   'carol@example.com', '9988776655');

COMMIT;

-- Verification
SELECT 'BOOKS created: '   || COUNT(*) AS STATUS FROM BOOKS;
SELECT 'MEMBERS created: ' || COUNT(*) AS STATUS FROM MEMBERS;
