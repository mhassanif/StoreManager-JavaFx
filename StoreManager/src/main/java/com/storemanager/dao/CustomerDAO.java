package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.Customer;
import com.storemanager.model.cart.ShoppingCart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Method to fetch the balance of a customer based on customerId
    public static double fetchCustomerBalance(int customerId) {
        double balance = 0.0;

        String sql = "SELECT balance FROM CUSTOMER WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balance;
    }

    // Setter method to update the balance of a customer in the database
    public static boolean setCustomerBalance(int customerId, double newBalance) {
        String sql = "UPDATE CUSTOMER SET balance = ? WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);  // Set the new balance
            stmt.setInt(2, customerId);     // Specify the customer to update

            int rowsAffected = stmt.executeUpdate();

            // If at least one row is affected, the update was successful
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Create a new customer
    public static boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO USERS (user_id, username, email, password, role, address, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String customerSql = "INSERT INTO CUSTOMER (user_id, balance) VALUES (?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement customerStmt = conn.prepareStatement(customerSql)) {

            // Insert user data into USERS table
            userStmt.setInt(1, customer.getId());
            userStmt.setString(2, customer.getUsername());
            userStmt.setString(3, customer.getEmail());
            userStmt.setString(4, customer.getPassword());
            userStmt.setString(5, "Customer");
            userStmt.setString(6, customer.getAddress());
            userStmt.setString(7, customer.getPhoneNumber());
            int affectedRows = userStmt.executeUpdate();

            if (affectedRows == 0) {
                return false; // Inserting user failed
            }

            // Get the auto-generated user_id from the USERS table
            try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1); // Get the generated user_id
                    // Insert into the CUSTOMER table
                    customerStmt.setInt(1, userId);
                    customerStmt.setDouble(2, customer.getBalance());
                    customerStmt.executeUpdate();
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create a customer using userId and balance
    public static boolean createCustomer(int userId) {
        String sql = "INSERT INTO CUSTOMER (user_id, balance) VALUES (?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDouble(2, 2000);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get a customer by customerId
    public static Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM USERS u INNER JOIN CUSTOMER c ON u.user_id = c.user_id WHERE c.customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create User object from the result set
                int userId = rs.getInt("user_id");
                String username = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                double balance = rs.getDouble("balance");

                // Create Customer object
                Customer customer = new Customer(customerId, userId, username, email, password, address, phone, balance);
                return customer;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get a customer by userId
    public static Customer getCustomerByUserId(int userId) {
        String sql = "SELECT * FROM USERS u INNER JOIN CUSTOMER c ON u.user_id = c.user_id WHERE u.user_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String username = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                double balance = rs.getDouble("balance");

                return new Customer(customerId, userId, username, email, password, address, phone, balance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update customer info (e.g., balance, address, phone)
    public static boolean updateCustomerInfo(String address, String phoneNumber, int customerId, double balance) {
        String sql = "UPDATE USERS u INNER JOIN CUSTOMER c ON u.user_id = c.user_id SET u.address = ?, u.phone = ?, c.balance = ? WHERE c.customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address);
            stmt.setString(2, phoneNumber);
            stmt.setDouble(3, balance);
            stmt.setInt(4, customerId);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update the customer (full customer update)
    public static boolean updateCustomer(Customer customer) {
        return updateCustomerInfo(customer.getAddress(), customer.getPhoneNumber(), customer.getCustomerId(), customer.getBalance());
    }

    // Delete customer by customerId
    public static ShoppingCart initializeShoppingCart(int customerId) {
        try (Connection connection = DBconnector.getConnection()) {
            // Step 1: Check if a shopping cart already exists for the customer
            String query = "SELECT cart_id FROM SHOPPINGCART WHERE customer_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customerId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int cartId = resultSet.getInt("cart_id");
                        return new ShoppingCart(cartId); // Return the cart with the retrieved ID
                    } else {
                        // Step 2: If no cart exists, create one for the customer
                        String insertQuery = "INSERT INTO SHOPPINGCART (customer_id) VALUES (?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            insertStatement.setInt(1, customerId);
                            insertStatement.executeUpdate();
                            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    int newCartId = generatedKeys.getInt(1);
                                    return new ShoppingCart(newCartId); // Return the cart with the new ID
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception for debugging
        }

        return null; // Return null if an error occurs
    }

    public static boolean deleteCustomerByUserId(int userId) {
        Customer customer = CustomerDAO.getCustomerByUserId(userId);
        if (customer == null) return false;

        int customerId = customer.getCustomerId();
        return deleteCustomer(customerId);
    }

    public static boolean deleteCustomer(int customerId) {
        Customer customer = getCustomerById(customerId);
        if (customer == null) return false;

        int userId = customer.getId();

        try {
            NotificationDAO.deleteNotificationsByUserId(userId);
            FeedbackDAO.deleteFeedbackByCustomer(customerId);
            ShoppingCartDAO.deleteCart(customer.getShoppingCart().getCartId());
            OrderDAO.deleteOrderByCustomer(customerId);

            String query = "DELETE FROM CUSTOMER WHERE customer_id = ?";
            try (Connection connection = DBconnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, customerId);
                if (preparedStatement.executeUpdate() > 0) {
                    return UserDAO.deleteUser(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Get all customers
    public static List<Customer> getAllCustomers() {
        String sql = "SELECT * FROM USERS u INNER JOIN CUSTOMER c ON u.user_id = c.user_id";

        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                double balance = rs.getDouble("balance");

                Customer customer = new Customer(customerId, userId, username, email, password, address, phone, balance);
                customers.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
