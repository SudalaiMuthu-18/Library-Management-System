package com.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Handles requests to the application root path and redirects
 * unauthenticated users to the login page. This avoids relying on
 * a static index.html file being present and ensures the route is
 * explicitly and publicly accessible.
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<?> rootRedirect() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login"))
                .build();
    }
}
