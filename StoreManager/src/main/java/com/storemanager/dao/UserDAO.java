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

    // Authenticate a user
    public static User authenticate(String username, String password) {
        String query = "SELECT * FROM USERS WHERE name = ? AND password = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToUser(resultSet); // If credentials match, map the row to a User object
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if authentication fails or an exception occurs
    }

    // Fetch staff_id by user_id
    public static int getStaffIdByUserId(int userId) {
        String query = "SELECT staff_id FROM STAFF WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("staff_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if not found
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
        String sql = "SELECT * FROM USERS WHERE name LIKE ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");  // Use the partial username in the query
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                String username = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phone = rs.getString("phone");

                if ("customer".equalsIgnoreCase(role)) {
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

                } else if ("staff".equalsIgnoreCase(role)) {
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



    // Overloaded createUser method that accepts a User object
    public static boolean createUser(User user) {
        String query = "INSERT INTO USERS (name, email, password, role, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Insert into USERS table
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getPhoneNumber());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated user_id
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1); // Generated user_id

                    // Create either Customer or Staff depending on role
                    if ("Customer".equals(user.getRole())) {
                        return CustomerDAO.createCustomer(userId);
                    } else if ("Staff".equals(user.getRole())) {
                        return StaffDAO.createStaff(user, "Warehouse Staff"); // Position for warehouse staff
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if insertion fails
    }

    // Overloaded createUser method that accepts individual attributes
    public static boolean createUser(String username, String email, String password, String role, String address, String phone) {
        String query = "INSERT INTO USERS (name, email, password, role, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Insert into USERS table
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, role);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, phone);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Get generated user_id
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1); // Generated user_id

                    // Create either Customer or Staff depending on role
                    if ("Customer".equals(role)) {
                        return CustomerDAO.createCustomer(userId);
                    } else if ("Staff".equals(role)) {
                        return StaffDAO.createStaff(new User(userId, username, email, password, role, address, phone), "Warehouse Staff"); // For staff, role is 'Staff' and position is passed
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if insertion fails
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