package org.example.pidev.models;

import java.time.LocalDateTime;

/**
 * FaceImage entity - Maps to Symfony face_images table
 * Stores user face images for recognition system
 */
public class FaceImage {
    private int id;
    private int userId;              // FK to utilisateur
    private String facePath;         // Path to face image file
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FaceImage() {
    }

    public FaceImage(int id, int userId, String facePath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.facePath = facePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public FaceImage(int userId, String facePath) {
        this.userId = userId;
        this.facePath = facePath;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FaceImage{" +
                "id=" + id +
                ", userId=" + userId +
                ", facePath='" + facePath + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

