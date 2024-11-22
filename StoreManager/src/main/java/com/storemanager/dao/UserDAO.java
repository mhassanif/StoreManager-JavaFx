package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Fetch user by ID
    public User getUserById(int userId) {
        String query = "SELECT * FROM USERS WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found or an exception occurs
    }

    // Fetch user by email
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM USERS WHERE email = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found or an exception occurs
    }

    // Create a new user
    public boolean createUser(User user) {
        String query = "INSERT INTO USERS (name, email, password, role, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getPhoneNumber());

            return preparedStatement.executeUpdate() > 0; // Return true if insert is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

    // Update an existing user
    public boolean updateUser(User user) {
        String query = "UPDATE USERS SET name = ?, email = ?, password = ?, role = ?, address = ?, phone = ? WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setInt(7, user.getId());

            return preparedStatement.executeUpdate() > 0; // Return true if update is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

    // Delete a user by ID
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM USERS WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            return preparedStatement.executeUpdate() > 0; // Return true if delete is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }

    // Map ResultSet to User object
    private User mapToUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        String role = resultSet.getString("role");
        String address = resultSet.getString("address");
        String phone = resultSet.getString("phone");

        return new User(id, name, email, password, role, address, phone);
    }
}
