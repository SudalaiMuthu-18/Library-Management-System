package com.library.util;

/**
 * Lightweight JSON utility — no external dependencies needed.
 * Parses simple flat JSON objects and escapes strings.
 */
public class JsonUtil {

    /**
     * Extracts a string or number value for the given key from a flat JSON object.
     * Supports: "key":"value"  and  "key":123
     */
    public static String parse(String json, String key) {
        if (json == null || key == null) return null;
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;

        idx += search.length();
        // Skip whitespace and colon
        while (idx < json.length() && (json.charAt(idx) == ' ' || json.charAt(idx) == ':')) idx++;
        if (idx >= json.length()) return null;

        char c = json.charAt(idx);
        if (c == '"') {
            // String value
            idx++;
            StringBuilder sb = new StringBuilder();
            while (idx < json.length() && json.charAt(idx) != '"') {
                if (json.charAt(idx) == '\\' && idx + 1 < json.length()) {
                    idx++;
                    char esc = json.charAt(idx);
                    switch (esc) {
                        case '"':  sb.append('"');  break;
                        case '\\': sb.append('\\'); break;
                        case 'n':  sb.append('\n'); break;
                        case 'r':  sb.append('\r'); break;
                        case 't':  sb.append('\t'); break;
                        default:   sb.append(esc);
                    }
                } else {
                    sb.append(json.charAt(idx));
                }
                idx++;
            }
            return sb.toString();
        } else {
            // Number or boolean
            StringBuilder sb = new StringBuilder();
            while (idx < json.length() &&
                   (Character.isDigit(json.charAt(idx))
                    || json.charAt(idx) == '-'
                    || json.charAt(idx) == '.')) {
                sb.append(json.charAt(idx++));
            }
            return sb.length() > 0 ? sb.toString() : null;
        }
    }

    /** Parses an integer field; returns 0 on failure. */
    public static int parseInt(String json, String key) {
        String val = parse(json, key);
        if (val == null || val.isEmpty()) return 0;
        try { return Integer.parseInt(val.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    /** Escapes a string for safe embedding in a JSON value. */
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /** Builds a simple error JSON response. */
    public static String error(String message) {
        return "{\"error\":\"" + esc(message) + "\"}";
    }

    /** Builds a simple success JSON response. */
    public static String success(String message) {
        return "{\"message\":\"" + esc(message) + "\"}";
    }
}
