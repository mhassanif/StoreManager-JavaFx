package com.storemanager.model.users;

import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Manager user in the system.
 * Inherits from the User class and provides functionality specific to the Manager role.
 */
public class Manager extends User {

    private int staffId; // Unique ID for the Manager in the STAFF table

    // Constructor for Manager
    public Manager(int staffId, int userId, String username, String email, String password, String address, String phoneNumber) {
        super(userId, username, email, password, "Manager", address, phoneNumber);
        this.staffId = staffId;
    }

    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    /**
     * View all orders in the system.
     * Returns a list of order details (e.g., order ID and order date).
     */
    public List<String> viewAllOrders() {
        List<String> orders = new ArrayList<>();
        String query = "SELECT order_id, order_date FROM ORDERTABLE"; // Assuming an ORDERTABLE exists

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String orderDetails = "Order ID: " + rs.getInt("order_id") + ", Date: " + rs.getDate("order_date");
                orders.add(orderDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Manage products by adding, updating, or deleting them.
     *
     * @param action      The action to perform: "add", "update", or "delete".
     * @param productName The name of the product.
     * @param price       The price of the product (ignored for "delete").
     */
    public boolean manageProducts(String action, String productName, double price) {
        String query = switch (action.toLowerCase()) {
            case "add" -> "INSERT INTO PRODUCT (product_name, price) VALUES (?, ?)";
            case "update" -> "UPDATE PRODUCT SET price = ? WHERE product_name = ?";
            case "delete" -> "DELETE FROM PRODUCT WHERE product_name = ?";
            default -> null;
        };

        if (query == null) {
            System.out.println("Invalid action. Please use 'add', 'update', or 'delete'.");
            return false;
        }

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if ("add".equalsIgnoreCase(action) || "update".equalsIgnoreCase(action)) {
                stmt.setString(1, productName);
                stmt.setDouble(2, price);
            } else if ("delete".equalsIgnoreCase(action)) {
                stmt.setString(1, productName);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product successfully " + action + "ed.");
                return true;
            } else {
                System.out.println("No product found to " + action + ".");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * View all customers in the system.
     * Returns a list of customer details (e.g., username and email).
     */
    public List<String> viewAllCustomers() {
        List<String> customers = new ArrayList<>();
        String query = "SELECT username, email FROM USERS WHERE role = 'Customer'";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String customerDetails = "Username: " + rs.getString("username") + ", Email: " + rs.getString("email");
                customers.add(customerDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Assign a new role to a user.
     *
     * @param username The username of the user to update.
     * @param role     The new role to assign (e.g., "Staff", "Customer").
     */
    public boolean assignRole(String username, String role) {
        String query = "UPDATE USERS SET role = ? WHERE username = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, role);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Role successfully updated for " + username);
                return true;
            } else {
                System.out.println("User not found to update role.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Logs the Manager out of the system.
     */
    @Override
    public void logout() {
        System.out.println("Manager " + getUsername() + " has logged out.");
    }

    @Override
    public String toString() {
        return "Manager{" +
                "staffId=" + staffId +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }
}
