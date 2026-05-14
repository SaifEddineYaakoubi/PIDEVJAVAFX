package org.example.pidev.models;

import java.time.LocalDateTime;

/**
 * UserBadge entity - Maps to Symfony user_badge table
 * Junction table for user-badge relationships
 */
public class UserBadge {
    private int id;
    private int userId;              // FK to utilisateur
    private int badgeId;             // FK to badge
    private LocalDateTime createdAt;

    public UserBadge() {
    }

    public UserBadge(int userId, int badgeId) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.createdAt = LocalDateTime.now();
    }

    public UserBadge(int id, int userId, int badgeId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.badgeId = badgeId;
        this.createdAt = createdAt;
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

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserBadge{" +
                "id=" + id +
                ", userId=" + userId +
                ", badgeId=" + badgeId +
                ", createdAt=" + createdAt +
                '}';
    }
}

