package com.storemanager.model.users;

import com.storemanager.db.DBconnector;
import java.sql.*;

public class WarehouseStaff extends User {

    public WarehouseStaff(String username, String email, String password, String role) {
        super(username, email, password, role);
    }

    // Functionality to view the list of products in the inventory
    public void viewInventory() {
        String query = "SELECT * FROM Inventory";  // Assuming the table is named Inventory

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            // Display all inventory items
            System.out.println("Inventory List:");
            while (resultSet.next()) {
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                System.out.println("Product: " + productName + ", Quantity: " + quantity + ", Price: " + price);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while retrieving inventory.");
        }
    }

    // Functionality to update inventory after an order is processed (decrease quantity)
    public void updateInventory(int productId, int quantitySold) {
        String query = "UPDATE Inventory SET quantity = quantity - ? WHERE product_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the parameters for the query
            pstmt.setInt(1, quantitySold);
            pstmt.setInt(2, productId);

            // Execute the update query
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Inventory updated successfully.");
            } else {
                System.out.println("Error: Could not update inventory.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while updating inventory.");
        }
    }

    // Functionality to process an order (Update the stock when an order is processed)
    public void processOrder(int orderId) {
        String query = "SELECT product_id, quantity FROM OrderDetails WHERE order_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, orderId);
            ResultSet resultSet = pstmt.executeQuery();

            // Process each item in the order
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                int quantitySold = resultSet.getInt("quantity");

                // Update inventory after order is processed
                updateInventory(productId, quantitySold);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while processing the order.");
        }
    }

    // Implement logout functionality for warehouse staff (inherited from User class)
    @Override
    public void logout() {
        System.out.println("Warehouse staff " + getUsername() + " has logged out.");
        // Additional logout logic can be implemented here if necessary
    }
}

