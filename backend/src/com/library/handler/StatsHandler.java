package com.library.handler;

import com.library.dao.BookDAO;
import com.library.dao.IssueDAO;
import com.library.dao.MemberDAO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET /api/stats  →  returns dashboard statistics as JSON.
 */
public class StatsHandler extends BaseHandler implements HttpHandler {

    private final BookDAO   bookDao   = new BookDAO();
    private final MemberDAO memberDao = new MemberDAO();
    private final IssueDAO  issueDao  = new IssueDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (handlePreflight(ex)) return;
        if (!"GET".equals(ex.getRequestMethod())) {
            sendError(ex, 405, "Method Not Allowed");
            return;
        }
        try {
            int[] bookCounts  = bookDao.getCounts();
            int   totalBooks  = bookCounts[0];
            int   available   = bookCounts[1];
            int   totalMem    = memberDao.getCount();
            int   issuedCount = issueDao.getIssuedCount();

            String json = String.format(
                "{\"totalBooks\":%d,\"availableBooks\":%d,\"totalMembers\":%d,\"issuedBooks\":%d}",
                totalBooks, available, totalMem, issuedCount
            );
            sendOk(ex, json);
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(ex, 500, "Database error: " + e.getMessage());
        }
    }
}
