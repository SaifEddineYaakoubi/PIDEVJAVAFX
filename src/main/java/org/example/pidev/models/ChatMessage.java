package org.example.pidev.models;

import java.time.LocalDateTime;

public class ChatMessage {
    private Long id;
    private String content;
    private String response;
    private String language;
    private LocalDateTime timestamp;
    private Long userId;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(String content, String language, Long userId) {
        this.content = content;
        this.language = language;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}