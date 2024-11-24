package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.User;
import com.storemanager.model.users.Admin;
import com.storemanager.model.users.Manager;
import com.storemanager.model.users.WarehouseStaff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public static List<User> getAllStaff() {
        List<User> staffList = new ArrayList<>();

        String query = "SELECT s.staff_id, s.user_id, s.position FROM STAFF s";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate through each staff record
            while (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");
                int userId = resultSet.getInt("user_id");
                String position = resultSet.getString("position");

                // Fetch user details using UserDAO
                User user = UserDAO.getUserById(userId);

                // If user exists, map to the appropriate staff type
                if (user != null) {
                    switch (position) {
                        case "Admin" -> staffList.add(
                                new Admin(staffId, user.getId(), user.getUsername(), user.getEmail(),
                                        user.getPassword(), user.getAddress(), user.getPhoneNumber()));
                        case "Manager" -> staffList.add(
                                new Manager(staffId, user.getId(), user.getUsername(), user.getEmail(),
                                        user.getPassword(), user.getAddress(), user.getPhoneNumber()));
                        case "WarehouseStaff" -> staffList.add(
                                new WarehouseStaff(staffId, user.getId(), user.getUsername(), user.getEmail(),
                                        user.getPassword(), user.getAddress(), user.getPhoneNumber()));
                        default -> throw new IllegalArgumentException("Unknown position: " + position);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList; // Return the list of staff members
    }

    // Fetch staff position by user_id
    public static String getStaffPositionByUserId(int userId) {
        String query = "SELECT position FROM STAFF WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("position");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    public static boolean createStaff(int userId, String position) {
        String sql = "INSERT INTO STAFF (user_id, position) VALUES (?, ?)";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, position);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createStaff(User user, String position) {
        String query = "INSERT INTO STAFF (user_id, position) VALUES (?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, user.getId());
            stmt.setString(2, position);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete staff by user_id
    public static boolean deleteStaffByUserId(int userId) {
        // First, get the staff_id using the user_id
        int staffId = getStaffIdByUserId(userId);
        if (staffId == -1) {
            return false; // No staff found for the given user_id
        }

        // Call the deleteStaff method to handle the deletion
        return deleteStaff(staffId);
    }

    // Delete staff by staff_id
    public static boolean deleteStaff(int staffId) {
        try {
            String query = "SELECT user_id FROM STAFF WHERE staff_id = ?";
            try (Connection connection = DBconnector.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, staffId);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");

                    // Delete related records using appropriate DAOs
                    NotificationDAO.deleteNotificationsByUserId(userId);

                    // Delete staff record
                    String deleteQuery = "DELETE FROM STAFF WHERE staff_id = ?";
                    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                        deleteStmt.setInt(1, staffId);
                        if (deleteStmt.executeUpdate() > 0) {
                            // After deleting the staff, delete the user
                            return UserDAO.deleteUser(userId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to get staff_id by user_id
    public static int getStaffIdByUserId(int userId) {
        String query = "SELECT staff_id FROM STAFF WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("staff_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no staff found for the given user_id
    }
}
