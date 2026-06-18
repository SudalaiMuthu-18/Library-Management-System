package com.library;

import com.library.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Application entry point.
 * Starts an embedded HTTP server on port 8080 and registers all API handlers.
 *
 * Run:  java -cp "out;lib/ojdbc11.jar" com.library.Main
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Register API & Static Web Page endpoints
        IssueHandler issueHandler = new IssueHandler();
        server.createContext("/api/books",   new BookHandler());
        server.createContext("/api/members", new MemberHandler());
        server.createContext("/api/issue",   issueHandler);
        server.createContext("/api/return",  issueHandler);
        server.createContext("/api/issued",  issueHandler);
        server.createContext("/api/stats",   new StatsHandler());
        server.createContext("/",            new StaticHandler());

        // Thread pool for concurrent requests
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   📚  Library Management System — STARTED   ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Web URL: http://localhost:" + PORT + "/             ║");
        System.out.println("║  API    : http://localhost:" + PORT + "/api/books    ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Visit the Web URL in your browser!          ║");
        System.out.println("║  Press  Ctrl+C  to stop the server          ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
}
