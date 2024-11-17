package com.storemanager.model.users;

import com.storemanager.db.DBconnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an admin user in the system.
 * Inherits from the User class and provides functionality specific to the admin role.
 */
public class Admin extends User {

    // Admin-specific fields can be added here, like a list of staff
    private List<String> managedUsers;

    // Constructor
    public Admin(String username, String email, String password) {
        super(username, email, password, "Admin"); // Assigning the role as "Admin"
        this.managedUsers = new ArrayList<>();
    }

    // Getter for managedUsers
    public List<String> getManagedUsers() {
        return managedUsers;
    }

    // Setters for managedUsers if needed
    public void setManagedUsers(List<String> managedUsers) {
        this.managedUsers = managedUsers;
    }

    /**
     * Override the logout method for the admin.
     * This could involve removing any session or admin-related cleanup.
     */
    @Override
    public void logout() {
        // Implement any session-related logout functionality
        System.out.println("Admin " + getUsername() + " has logged out.");
    }

    /**
     * Admin can add a new user to the system (e.g., add new customers, staff, or even another admin).
     */
    public boolean addUser(String username, String email, String password, String role) {
        String query = "INSERT INTO Users (username, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBconnector.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, role);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0; // Return true if user is added successfully
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Admin can delete an existing user from the system.
     */
    public boolean deleteUser(String username) {
        String query = "DELETE FROM Users WHERE username = ?";

        try (Connection connection = DBconnector.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0; // Return true if user is deleted successfully
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Admin can view all users in the system.
     */
    public List<String> viewAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM Users";

        try (Connection connection = DBconnector.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet resultSet = stmt.executeQuery()) {

                while (resultSet.next()) {
                    users.add(resultSet.getString("username"));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Admin can update a user's role (e.g., change a user's role to Manager, Customer, etc.).
     */
    public boolean updateUserRole(String username, String newRole) {
        String query = "UPDATE Users SET role = ? WHERE username = ?";

        try (Connection connection = DBconnector.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, newRole);
                stmt.setString(2, username);

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0; // Return true if role is updated successfully
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Admin can search for a user by username to check their details.
     */
    public static Customer searchUserByUsername(String username) {
        String query = "SELECT username, email, password, address, phoneNumber FROM Users WHERE username = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);  // Set the username parameter for the query

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String address = rs.getString("address");
                    String phoneNumber = rs.getString("phoneNumber");
                    // Now create a Customer object with all the required fields
                    return new Customer(username, email, password, address, phoneNumber);
                } else {
                    System.out.println("User not found.");
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public String toString() {
        return "Admin{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
