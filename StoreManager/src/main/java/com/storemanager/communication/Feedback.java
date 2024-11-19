package com.storemanager.communication;

import java.util.Objects;

/**
 * Represents customer feedback in the system.
 */
public class Feedback {

    private int id;              // Unique identifier for the feedback
    private int customerId;      // ID of the customer who provided the feedback
    private String comments;     // Feedback comments

    // Constructor
    public Feedback(int id, int customerId, String comments) {
        this.id = id;
        this.customerId = customerId;
        this.comments = comments;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // Method to check if feedback is meaningful (e.g., not empty or too short)
    public boolean isMeaningful() {
        return comments != null && comments.trim().length() >= 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return id == feedback.id && customerId == feedback.customerId &&
                Objects.equals(comments, feedback.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, comments);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", comments='" + comments + '\'' +
                '}';
    }
}
