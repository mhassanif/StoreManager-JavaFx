package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.User;
import com.storemanager.model.users.Admin;
import com.storemanager.model.users.Manager;
import com.storemanager.model.users.WarehouseStaff;

import java.sql.*;

public class StaffDAO {

    private final UserDAO userDAO;

    public StaffDAO() {
        this.userDAO = new UserDAO(); // Initialize the UserDAO for user-related operations
    }

    public User getStaffById(int staffId) {
        User user = null;

        String staffQuery = "SELECT s.staff_id, s.user_id, s.position "
                + "FROM STAFF s "
                + "WHERE s.staff_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(staffQuery)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();

            // If the result set contains a record, process it
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String position = rs.getString("position");

                // Use UserDAO to retrieve user details
                user = userDAO.getUserById(userId);

                // If the user exists, create an instance of the appropriate staff type based on position
                if (user != null) {
                    user = switch (position) {
                        case "Admin" -> new Admin(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                                user.getAddress(), user.getPhoneNumber());
                        case "Manager" ->
                                new Manager(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                                        user.getAddress(), user.getPhoneNumber());
                        case "WarehouseStaff" ->
                                new WarehouseStaff(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                                        user.getAddress(), user.getPhoneNumber());
                        default -> throw new IllegalArgumentException("Unknown position: " + position);
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;  // Return the User object (could be Admin, Manager, or WarehouseStaff)
    }

    public boolean createStaff(User user, String position) {
        try {
            // Step 1: Use UserDAO to create the user
            boolean userCreated = userDAO.createUser(user);
            if (!userCreated) {
                return false; // Return false if user creation fails
            }

            // Step 2: Insert the staff-specific data into the STAFF table
            String staffQuery = "INSERT INTO STAFF (user_id, position) VALUES (?, ?)";
            try (Connection conn = DBconnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(staffQuery)) {
                stmt.setInt(1, user.getId());
                stmt.setString(2, position);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;  // Return true if staff was created
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if an error occurs
    }

    public boolean updateStaff(User user, String position) {
        try {
            // Step 1: Use UserDAO to update the user
            boolean userUpdated = userDAO.updateUser(user);
            if (!userUpdated) {
                return false; // Return false if user update fails
            }

            // Step 2: Update the staff-specific data in the STAFF table
            String staffQuery = "UPDATE STAFF SET position = ? WHERE user_id = ?";
            try (Connection conn = DBconnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(staffQuery)) {
                stmt.setString(1, position);
                stmt.setInt(2, user.getId());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;  // Return true if staff was updated
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if an error occurs
    }

    public boolean deleteStaff(int staffId) {
        try {
            // Step 1: Use UserDAO to delete the user
            boolean userDeleted = userDAO.deleteUser(staffId);
            if (!userDeleted) {
                return false;  // Return false if user deletion fails
            }

            // Step 2: Delete the staff record from the STAFF table
            String staffQuery = "DELETE FROM STAFF WHERE staff_id = ?";
            try (Connection conn = DBconnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(staffQuery)) {
                stmt.setInt(1, staffId);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;  // Return true if staff was deleted
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if an error occurs
    }
}
