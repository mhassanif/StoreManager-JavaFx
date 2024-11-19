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

    private List<String> managedUsers;

    // Constructor
    public Admin(int id, String username, String email, String password, String address, String phoneNumber) {
        super(id, username, email, password, "Admin", address, phoneNumber); // Assigning the role as "Admin"
        this.managedUsers = new ArrayList<>();
    }

    // Getter for managedUsers
    public List<String> getManagedUsers() {
        return managedUsers;
    }

    public void setManagedUsers(List<String> managedUsers) {
        this.managedUsers = managedUsers;
    }

    @Override
    public void logout() {
        System.out.println("Admin " + getUsername() + " has logged out.");
    }

    public boolean addUser(String username, String email, String password, String role, String address, String phoneNumber) {
        String query = "INSERT INTO Users (username, email, password, role, address, phone_number) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.setString(5, address);
            stmt.setString(6, phoneNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM Users WHERE username = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> viewAllUsers() {
        List<String> users = new ArrayList<>();
        String query = "SELECT username FROM Users";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                users.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean updateUserRole(String username, String newRole) {
        String query = "UPDATE Users SET role = ? WHERE username = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newRole);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Customer searchUserByUsername(String username) {
        String query = "SELECT user_id, username, email, password, address, phone_number FROM Users WHERE username = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String address = rs.getString("address");
                    String phoneNumber = rs.getString("phone_number");
                    return new Customer(id, username, email, password, address, phoneNumber);
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
                ", address='" + getAddress() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }
}
