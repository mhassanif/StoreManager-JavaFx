package com.storemanager.dao;

import com.storemanager.communication.Notification;
import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // Fetch notification by notification ID
    public static Notification getNotificationById(int notificationId) {
        String query = "SELECT notification_id, message, date FROM NOTIFICATION WHERE notification_id = ?";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String message = rs.getString("message");
                Timestamp date = rs.getTimestamp("date");
                return new Notification(notificationId, message, date.toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fetch all notifications for a specific user
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT n.notification_id, n.message, n.date, nr.status FROM NOTIFICATION n " +
                "JOIN NOTIFICATION_RECIPIENT nr ON n.notification_id = nr.notification_id " +
                "WHERE nr.user_id = ?";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("notification_id");
                String message = rs.getString("message");
                Timestamp date = rs.getTimestamp("date");
                String status = rs.getString("status");
                notifications.add(new Notification(id, message, date.toLocalDateTime(), status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // Mark a notification as read for a user
    public static boolean markNotificationAsRead(int notificationId, int userId) {
        String query = "UPDATE NOTIFICATION_RECIPIENT SET status = 'Read' WHERE notification_id = ? AND user_id = ?";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create a new notification and associate it with a list of user IDs
    public static boolean createNotification(Notification notification, List<Integer> userIds) {
        String notificationQuery = "INSERT INTO NOTIFICATION (message, date) VALUES (?, ?)";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(notificationQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, notification.getMessage());
            stmt.setTimestamp(2, Timestamp.valueOf(notification.getDate()));
            int result = stmt.executeUpdate();
            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int notificationId = generatedKeys.getInt(1);

                    // Insert into NOTIFICATION_RECIPIENT table for each user
                    String recipientQuery = "INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id, status) VALUES (?, ?, 'Unread')";
                    try (PreparedStatement recipientStmt = DBconnector.getConnection().prepareStatement(recipientQuery)) {
                        for (int userId : userIds) {
                            recipientStmt.setInt(1, notificationId);
                            recipientStmt.setInt(2, userId);
                            recipientStmt.addBatch();
                        }
                        recipientStmt.executeBatch();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a notification from NOTIFICATION_RECIPIENT table (notification could belong to multiple users)
    public static boolean deleteNotification(int notificationId) {
        String query = "DELETE FROM NOTIFICATION_RECIPIENT WHERE notification_id = ?";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete notifications for a user from NOTIFICATION_RECIPIENT table
    public static boolean deleteNotificationsByUserId(int userId) {
        String query = "DELETE FROM NOTIFICATION_RECIPIENT WHERE user_id = ?";
        try (PreparedStatement stmt = DBconnector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
