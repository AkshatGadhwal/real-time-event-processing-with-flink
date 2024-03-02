package com.example.flink;

public class UserEvent {
    public long timestamp;
    public String userId;
    public String eventType;
    public String productId;
    public int sessionDuration;

    // Constructors, getters, and setters
    public UserEvent() {
    }

    public UserEvent(long timestamp, String userId, String eventType, String productId, int sessionDuration) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.productId = productId;
        this.sessionDuration = sessionDuration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
