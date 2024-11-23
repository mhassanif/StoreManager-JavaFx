package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.Customer;
import com.storemanager.model.users.User;
import com.storemanager.model.users.WarehouseStaff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM USERS";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapToUser(resultSet)); // Map each row to a User object and add to the list
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users; // Return the list of users
    }

    // Fetch user by ID
    public static User getUserById(int userId) {
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
    public static User getUserByEmail(String email) {
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

    public static List<User> searchUsers(String query) {
        List<User> users = new ArrayList<>();
        // Search for users based on partial username
        String sql = "SELECT * FROM USERS WHERE username LIKE ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");  // Use the partial username in the query
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phone = rs.getString("phone");

                if ("Customer".equals(role)) {
                    // Query the CUSTOMER table to fetch customer_id based on the user_id
                    String customerQuery = "SELECT * FROM CUSTOMER WHERE user_id = ?";
                    try (PreparedStatement customerStmt = conn.prepareStatement(customerQuery)) {
                        customerStmt.setInt(1, userId);
                        try (ResultSet customerRs = customerStmt.executeQuery()) {
                            if (customerRs.next()) {
                                int customerId = customerRs.getInt("customer_id");
                                // Create a Customer object with the relevant information
                                users.add(new Customer(customerId, userId, username, email, password, address, phone));
                            }
                        }
                    }

                } else if ("WarehouseStaff".equals(role)) {
                    // Query the STAFF table to fetch staff_id based on the user_id
                    String staffQuery = "SELECT * FROM STAFF WHERE user_id = ?";
                    try (PreparedStatement staffStmt = conn.prepareStatement(staffQuery)) {
                        staffStmt.setInt(1, userId);
                        try (ResultSet staffRs = staffStmt.executeQuery()) {
                            if (staffRs.next()) {
                                int staffId = staffRs.getInt("staff_id");
                                // Create a WarehouseStaff object with the relevant information
                                users.add(new WarehouseStaff(staffId, userId, username, email, password, address, phone));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }



    // Create a new user
    public static boolean createUser(User user) {
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
    public static boolean updateUser(User user) {
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
    public static boolean deleteUser(int userId) {
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
    private static User mapToUser(ResultSet resultSet) throws SQLException {
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