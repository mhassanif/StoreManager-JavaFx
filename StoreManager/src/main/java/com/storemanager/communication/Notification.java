package com.storemanager.communication;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a notification in the system.
 */
public class Notification {

    private int id;                // Unique identifier for the notification
    private String message;        // The notification message
    private LocalDateTime date;    // Date and time the notification was created
    private String status;         // Read/Unread status of the notification

    // Constructor
    public Notification(int id, String message, LocalDateTime date, String status) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.status = status;
    }

    // Constructor without status (defaulting to 'Unread')
    public Notification(int id, String message, LocalDateTime date) {
        this(id, message, date, "Unread");
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Mark the notification as read
    public void markAsRead() {
        this.status = "Read";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return id == that.id && Objects.equals(message, that.message) &&
                Objects.equals(date, that.date) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, date, status);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
}

