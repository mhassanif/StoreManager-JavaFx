package com.storemanager.model.users;

import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Manager extends User {

    // Constructor for Manager, calling the superclass User constructor
    public Manager(String username, String email, String password, String role) {
        super(username, email, password, role);
    }

    // Method for viewing all orders (just a sample task)
    public List<String> viewAllOrders() {
        List<String> orders = new ArrayList<>();
        String query = "SELECT order_id, order_date FROM Orders"; // Assuming an Orders table exists

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String orderDetails = "Order ID: " + rs.getString("order_id") + ", Date: " + rs.getString("order_date");
                orders.add(orderDetails);  // Add order details to list
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders; // Return the list of orders
    }

    // Method to manage products (add/update/delete)
    public void manageProducts(String action, String productName, double price) {
        // action could be "add", "update", or "delete"
        String query = "";
        if (action.equals("add")) {
            query = "INSERT INTO Products (product_name, price) VALUES (?, ?)";
        } else if (action.equals("update")) {
            query = "UPDATE Products SET price = ? WHERE product_name = ?";
        } else if (action.equals("delete")) {
            query = "DELETE FROM Products WHERE product_name = ?";
        }

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (action.equals("add") || action.equals("update")) {
                stmt.setString(1, productName);
                stmt.setDouble(2, price);
            } else if (action.equals("delete")) {
                stmt.setString(1, productName);
            }

            int rowsAffected = stmt.executeUpdate(); // Execute the update query

            if (rowsAffected > 0) {
                System.out.println("Product successfully " + action + "ed.");
            } else {
                System.out.println("No product found to " + action + ".");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method for viewing all customers (Admin functionality)
    public List<String> viewAllCustomers() {
        List<String> customers = new ArrayList<>();
        String query = "SELECT username, email FROM Users WHERE role = 'Customer'";

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String customerDetails = "Username: " + rs.getString("username") + ", Email: " + rs.getString("email");
                customers.add(customerDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers; // Return the list of customers
    }

    // Method for assigning or managing roles for staff (if applicable)
    public void assignRole(String username, String role) {
        String query = "UPDATE Users SET role = ? WHERE username = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, role); // Set new role (e.g., Staff, Customer)
            stmt.setString(2, username); // Set username

            int rowsAffected = stmt.executeUpdate(); // Execute the update query

            if (rowsAffected > 0) {
                System.out.println("Role successfully updated for " + username);
            } else {
                System.out.println("User not found to update role.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        // Implement any session-related logout functionality
        System.out.println("Manager " + getUsername() + " has logged out.");
    }
}

