package com.library.controller;

import com.library.dto.ForgotPasswordRequest;
import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.security.CustomUserDetailsService;
import com.library.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    /**
     * POST /api/auth/login
     * Authenticates the user and returns a JWT token along with user info.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Retrieve user entity for additional info
            User user = userDetailsService.loadUserEntityByUsername(loginRequest.getUsernameOrEmail());

            LoginResponse loginResponse = new LoginResponse(
                    jwt,
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole()
            );

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INVALID_CREDENTIALS");
            error.put("message", "Invalid username/email or password. Please try again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "AUTH_ERROR");
            error.put("message", "Authentication failed. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/auth/me
     * Returns the current authenticated user's details.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "UNAUTHORIZED");
            error.put("message", "No authenticated user found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User user = userDetailsService.loadUserEntityByUsername(userDetails.getUsername());
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("fullName", user.getFullName());
        userData.put("role", user.getRole());
        return ResponseEntity.ok(userData);
    }

    /**
     * POST /api/auth/forgot-password
     * Handles forgot password requests. In production, would send a reset email.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Map<String, String> response = new HashMap<>();
        boolean emailExists = userRepository.existsByEmail(request.getEmail());

        // Always return success to prevent user enumeration
        response.put("message", "If an account with that email exists, you will receive a password reset link shortly.");
        response.put("status", "EMAIL_SENT");
        return ResponseEntity.ok(response);
    }
}
