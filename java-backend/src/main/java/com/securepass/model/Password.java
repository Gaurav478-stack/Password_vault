package com.securepass.model;

import java.time.LocalDateTime;

/**
 * Password entity representing a stored password entry
 * Demonstrates Encapsulation - private fields with getters/setters
 */
public class Password {
    private String id;
    private String service;
    private String username;
    private String encryptedPassword;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;
    private LocalDateTime lastAccessedAt;

    // Constructor
    public Password(String id, String service, String username, String encryptedPassword, String category) {
        this.id = id;
        this.service = service;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accessCount = 0;
    }

    // Getters and Setters (Encapsulation)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    // Business method
    public void recordAccess() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Password{" +
                "id='" + id + '\'' +
                ", service='" + service + '\'' +
                ", username='" + username + '\'' +
                ", category='" + category + '\'' +
                ", accessCount=" + accessCount +
                '}';
    }
}
