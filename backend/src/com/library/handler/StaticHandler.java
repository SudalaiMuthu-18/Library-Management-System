package com.library.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;

/**
 * Serves static frontend files (HTML, CSS, JS) directly from the server.
 * Makes the application self-contained and avoids CORS issues.
 */
public class StaticHandler implements HttpHandler {

    private static final String PRIMARY_PATH = "../frontend";
    private static final String SECONDARY_PATH = "./frontend";

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }

        // Search in primary then secondary path
        File file = new File(PRIMARY_PATH, path);
        if (!file.exists()) {
            file = new File(SECONDARY_PATH, path);
        }

        if (!file.exists() || file.isDirectory()) {
            String err = "404 Not Found";
            ex.sendResponseHeaders(404, err.length());
            try (OutputStream os = ex.getResponseBody()) {
                os.write(err.getBytes());
            }
            return;
        }

        // Determine MIME Content-Type
        String mime = "text/plain";
        if (path.endsWith(".html")) mime = "text/html; charset=UTF-8";
        else if (path.endsWith(".css")) mime = "text/css; charset=UTF-8";
        else if (path.endsWith(".js")) mime = "text/javascript; charset=UTF-8";
        else if (path.endsWith(".png")) mime = "image/png";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) mime = "image/jpeg";
        else if (path.endsWith(".ico")) mime = "image/x-icon";
        else if (path.endsWith(".svg")) mime = "image/svg+xml";

        ex.getResponseHeaders().set("Content-Type", mime);
        ex.sendResponseHeaders(200, file.length());

        try (OutputStream os = ex.getResponseBody();
             FileInputStream fis = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = fis.read(buf)) != -1) {
                os.write(buf, 0, n);
            }
        }
    }
}
