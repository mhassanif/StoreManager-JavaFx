package com.storemanager.dao;

import com.storemanager.communication.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.storemanager.db.DBconnector;

public class FeedbackDAO {

    // Method to fetch all feedback of all customers
    public static List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT feedback_id, customer_id, comments FROM FEEDBACK";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int feedbackId = rs.getInt("feedback_id");
                int customerId = rs.getInt("customer_id");
                String comments = rs.getString("comments");
                feedbackList.add(new Feedback(feedbackId, customerId, comments));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return feedbackList;
    }

    // Fetch feedback by customer ID
    public static List<Feedback> getFeedbackByCustomerId(int customerId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT feedback_id, customer_id, comments FROM FEEDBACK WHERE customer_id = ?";
        try (Connection connection = DBconnector.getConnection();PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("feedback_id");
                String comments = rs.getString("comments");
                feedbackList.add(new Feedback(id, customerId, comments));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    // Create a new feedback entry
    public static boolean createFeedback(Feedback feedback) {
        String query = "INSERT INTO FEEDBACK (customer_id, comments) VALUES (?, ?)";
        try (Connection connection = DBconnector.getConnection();PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, feedback.getCustomerId());
            stmt.setString(2, feedback.getComments());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a feedback entry by feedback ID
    public static boolean deleteFeedback(int feedbackId) {
        String query = "DELETE FROM FEEDBACK WHERE feedback_id = ?";
        try (Connection connection = DBconnector.getConnection();PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, feedbackId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete feedback entries for a specific customer
    public static boolean deleteFeedbackByCustomer(int customerId) {
        String query = "DELETE FROM FEEDBACK WHERE customer_id = ?";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, customerId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}