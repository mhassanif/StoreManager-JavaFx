package com.storemanager.model.users;

import com.storemanager.db.DBconnector;

import java.sql.*;

/**
 * Represents a Warehouse Staff user in the system.
 * Inherits from the User class and provides functionality specific to managing inventory.
 */
public class WarehouseStaff extends User {

    // Constructor for WarehouseStaff
    public WarehouseStaff(int id, String username, String email, String password, String address, String phoneNumber) {
        super(id, username, email, password, "WarehouseStaff", address, phoneNumber);
    }

    /**
     * View the inventory list, including product name, quantity, and price.
     */
    public void viewInventory() {
        String query = "SELECT product_name, quantity, price FROM Inventory"; // Assuming the table is named Inventory

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {

            System.out.println("Inventory List:");
            while (resultSet.next()) {
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                System.out.println("Product: " + productName + ", Quantity: " + quantity + ", Price: " + price);
            }

        } catch (SQLException e) {
            System.out.println("Error while retrieving inventory.");
            e.printStackTrace();
        }
    }

    /**
     * Update the inventory after an order is processed by decreasing the quantity.
     *
     * @param productId    The ID of the product to update.
     * @param quantitySold The quantity sold to decrease from inventory.
     */
    public boolean updateInventory(int productId, int quantitySold) {
        String query = "UPDATE Inventory SET quantity = quantity - ? WHERE product_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, quantitySold);
            pstmt.setInt(2, productId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Inventory updated successfully for Product ID: " + productId);
                return true;
            } else {
                System.out.println("Error: Product ID " + productId + " not found or insufficient quantity.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error while updating inventory.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Process an order by updating the inventory based on the order details.
     *
     * @param orderId The ID of the order to process.
     */
    public void processOrder(int orderId) {
        String query = "SELECT product_id, quantity FROM OrderDetails WHERE order_id = ?"; // Assuming OrderDetails table exists

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, orderId);
            ResultSet resultSet = pstmt.executeQuery();

            System.out.println("Processing Order ID: " + orderId);
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                int quantitySold = resultSet.getInt("quantity");

                // Update inventory for each product in the order
                boolean success = updateInventory(productId, quantitySold);
                if (success) {
                    System.out.println("Updated inventory for Product ID: " + productId);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while processing order ID: " + orderId);
            e.printStackTrace();
        }
    }

    /**
     * Logs the Warehouse Staff out of the system.
     */
    @Override
    public void logout() {
        System.out.println("Warehouse staff " + getUsername() + " has logged out.");
    }

    @Override
    public String toString() {
        return "WarehouseStaff{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }
}
