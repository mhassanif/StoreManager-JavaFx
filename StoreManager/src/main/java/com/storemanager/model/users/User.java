package com.storemanager.model.users;

import com.storemanager.db.DBconnector; // Import DBconnector for getting connection

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a generalized user in the system.
 * The User class serves as the base class for specific user roles such as Customer, StoreManager, and Admin.
 */
public class User {
    private int userId;
    private String username; // User's unique username
    private String email;    // User's email address
    private String password; // User's password
    private String role;     // Role of the user (e.g., "Customer", "Manager", "Admin", "Staff")
    private String address;
    private String phoneNumber;

    // Constructor
    public User(int id, String username, String email, String password, String role, String address,
                String Number) {
        this.userId = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.phoneNumber = Number;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return userId;
    }

    public void setId(int id) {
        this.userId = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    // abstraction removed , child classes can still override
    public void logout(){
        System.out.println( "User has logged out.");
    }

    /**
     * Login method that is common to all user types.
     * Checks user credentials against the database to authenticate the user.
     */
    public boolean login() {
        // Get connection from DBconnector class
        try (Connection connection = DBconnector.getConnection()) {

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, this.username);
                preparedStatement.setString(2, this.password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // If credentials are valid, populate user role
                        this.role = resultSet.getString("role");
                        return true; // Login successful
                    } else {
                        return false; // Login failed
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // In case of error, login failed
        }
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}