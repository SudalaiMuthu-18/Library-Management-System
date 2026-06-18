package com.library.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Base class for all HTTP handlers.
 * Provides common helpers: CORS headers, read request body, send response.
 */
public abstract class BaseHandler {

    // ─── CORS ─────────────────────────────────────────────────────────────────

    protected void addCors(HttpExchange ex) {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /** Handles preflight OPTIONS request. */
    protected boolean handlePreflight(HttpExchange ex) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            addCors(ex);
            ex.sendResponseHeaders(204, -1);
            ex.close();
            return true;
        }
        return false;
    }

    // ─── Response helpers ─────────────────────────────────────────────────────

    protected void sendJson(HttpExchange ex, int code, String json) throws IOException {
        addCors(ex);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
        ex.close();
    }

    protected void sendOk(HttpExchange ex, String json) throws IOException {
        sendJson(ex, 200, json);
    }

    protected void sendCreated(HttpExchange ex, String json) throws IOException {
        sendJson(ex, 201, json);
    }

    protected void sendError(HttpExchange ex, int code, String message) throws IOException {
        sendJson(ex, code, "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}");
    }

    // ─── Request body ─────────────────────────────────────────────────────────

    protected String readBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody();
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // ─── URL helpers ──────────────────────────────────────────────────────────

    /**
     * Extracts the last path segment as an integer.
     * e.g. /api/books/42  → 42
     * Returns -1 if not found.
     */
    protected int extractId(HttpExchange ex) {
        String path = ex.getRequestURI().getPath();
        String[] parts = path.split("/");
        try {
            String last = parts[parts.length - 1];
            return Integer.parseInt(last);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the value of a query parameter.
     * e.g. query = "search=java" → parseParam("search") = "java"
     */
    protected String parseParam(HttpExchange ex, String param) {
        String query = ex.getRequestURI().getQuery();
        if (query == null) return null;
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(param)) {
                try {
                    return java.net.URLDecoder.decode(kv[1], "UTF-8");
                } catch (Exception e) {
                    return kv[1];
                }
            }
        }
        return null;
    }
}
