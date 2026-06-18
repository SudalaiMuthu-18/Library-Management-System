package com.library.handler;

import com.library.dao.MemberDAO;
import com.library.model.Member;
import com.library.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles all /api/members requests.
 *
 * GET    /api/members         → list all (optional ?search=keyword)
 * POST   /api/members         → add member
 * PUT    /api/members/{id}    → update member
 * DELETE /api/members/{id}    → delete member
 */
public class MemberHandler extends BaseHandler implements HttpHandler {

    private final MemberDAO dao = new MemberDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (handlePreflight(ex)) return;

        String method = ex.getRequestMethod();
        String path   = ex.getRequestURI().getPath();
        boolean isCollection = path.matches("^/api/members/?$");

        try {
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
        List<Member> members = (search != null && !search.isEmpty())
            ? dao.searchMembers(search)
            : dao.getAllMembers();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < members.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(members.get(i).toJson());
        }
        sb.append("]");
        sendOk(ex, sb.toString());
    }

    // ─── POST Add ─────────────────────────────────────────────────────────────

    private void handleAdd(HttpExchange ex) throws IOException, SQLException {
        String body  = readBody(ex);
        String name  = JsonUtil.parse(body, "name");
        String email = JsonUtil.parse(body, "email");
        String phone = JsonUtil.parse(body, "phone");

        if (name == null || name.isEmpty()) {
            sendError(ex, 400, "Name is required.");
            return;
        }

        Member member = new Member(0, name, email, phone);
        boolean ok = dao.addMember(member);
        if (ok) sendCreated(ex, JsonUtil.success("Member registered successfully."));
        else    sendError(ex, 500, "Failed to add member.");
    }

    // ─── PUT Update ───────────────────────────────────────────────────────────

    private void handleUpdate(HttpExchange ex) throws IOException, SQLException {
        int id = extractId(ex);
        if (id == -1) { sendError(ex, 400, "Invalid member ID."); return; }

        String body  = readBody(ex);
        String name  = JsonUtil.parse(body, "name");
        String email = JsonUtil.parse(body, "email");
        String phone = JsonUtil.parse(body, "phone");

        if (name == null || name.isEmpty()) {
            sendError(ex, 400, "Name is required.");
            return;
        }

        Member member = new Member(id, name, email, phone);
        boolean ok = dao.updateMember(member);
        if (ok) sendOk(ex, JsonUtil.success("Member updated successfully."));
        else    sendError(ex, 404, "Member not found.");
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    private void handleDelete(HttpExchange ex) throws IOException, SQLException {
        int id = extractId(ex);
        if (id == -1) { sendError(ex, 400, "Invalid member ID."); return; }

        boolean ok = dao.deleteMember(id);
        if (ok) sendOk(ex, JsonUtil.success("Member deleted successfully."));
        else    sendError(ex, 404, "Member not found.");
    }
}
