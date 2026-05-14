package org.example.pidev.models;

import java.time.LocalDateTime;

/**
 * ResetPasswordRequest entity - Maps to Symfony reset_password_request table
 * Stores password reset tokens for security
 */
public class ResetPasswordRequest {
    private int id;
    private int userId;              // FK to utilisateur
    private String selector;         // Token selector (20 chars)
    private String hashedToken;      // Hashed token for verification
    private LocalDateTime requestedAt;
    private LocalDateTime expiresAt;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(int userId, String selector, String hashedToken, LocalDateTime expiresAt) {
        this.userId = userId;
        this.selector = selector;
        this.hashedToken = hashedToken;
        this.requestedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public ResetPasswordRequest(int id, int userId, String selector, String hashedToken,
                               LocalDateTime requestedAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.selector = selector;
        this.hashedToken = hashedToken;
        this.requestedAt = requestedAt;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public void setHashedToken(String hashedToken) {
        this.hashedToken = hashedToken;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Check if reset token has expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", selector='" + selector + '\'' +
                ", requestedAt=" + requestedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}

