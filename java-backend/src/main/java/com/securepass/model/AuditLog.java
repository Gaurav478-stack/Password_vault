package com.securepass.model;

import java.time.LocalDateTime;

/**
 * Audit Log entry for tracking vault activities
 * Demonstrates Encapsulation and Immutability
 */
public class AuditLog {
    private final String id;
    private final String action;
    private final String userId;
    private final String resourceId;
    private final String details;
    private final LocalDateTime timestamp;
    private final String ipAddress;
    private final boolean success;

    // Builder Pattern for complex object creation
    public static class Builder {
        private String id;
        private String action;
        private String userId;
        private String resourceId;
        private String details;
        private LocalDateTime timestamp;
        private String ipAddress;
        private boolean success;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public AuditLog build() {
            if (timestamp == null) {
                timestamp = LocalDateTime.now();
            }
            return new AuditLog(this);
        }
    }

    private AuditLog(Builder builder) {
        this.id = builder.id;
        this.action = builder.action;
        this.userId = builder.userId;
        this.resourceId = builder.resourceId;
        this.details = builder.details;
        this.timestamp = builder.timestamp;
        this.ipAddress = builder.ipAddress;
        this.success = builder.success;
    }

    // Getters only (Immutable object)
    public String getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getUserId() {
        return userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id='" + id + '\'' +
                ", action='" + action + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }
}
