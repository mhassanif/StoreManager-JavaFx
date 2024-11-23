package com.storemanager.dao;

import com.storemanager.communication.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.storemanager.db.DBconnector;

public class FeedbackDAO {

    private final Connection connection;

    public FeedbackDAO() throws SQLException {
        connection = DBconnector.getConnection();
    }

    // Fetch feedback by customer ID
    public List<Feedback> getFeedbackByCustomerId(int customerId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT feedback_id, customer_id, comments FROM FEEDBACK WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
    public boolean createFeedback(Feedback feedback) {
        String query = "INSERT INTO FEEDBACK (customer_id, comments) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
    public boolean deleteFeedback(int feedbackId) {
        String query = "DELETE FROM FEEDBACK WHERE feedback_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, feedbackId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
