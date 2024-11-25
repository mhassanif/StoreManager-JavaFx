package com.storemanager.model.users;

import com.storemanager.db.DBconnector;
import com.storemanager.model.items.InventoryProduct;
import com.storemanager.model.items.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Warehouse Staff user in the system.
 * Inherits from the User class and provides functionality specific to managing inventory.
 */
public class WarehouseStaff extends User {

    private int staffId; // Unique ID for the Warehouse Staff in the STAFF table
    String position;
    // Constructor for WarehouseStaff
    public WarehouseStaff(int staffId, int userId, String username, String email, String password, String address, String phoneNumber) {
        super(userId, username, email, password, "Staff", address, phoneNumber);
        this.staffId = staffId;
        position="Manager";
    }

    // Getter for staffId
    public int getStaffId() {
        return staffId;
    }

    /**
     * View the inventory list, including product name, quantity, and price.
     */
    public List<InventoryProduct> viewInventory() {
        List<InventoryProduct> inventoryList = new ArrayList<>();

        // SQL query to fetch inventory and product details
        String query = "SELECT " +
                "    INVENTORY.product_id, " +
                "    PRODUCT.name AS product_name, " +  // Adjust column name
                "    PRODUCT.price, " +
                "    INVENTORY.stock_quantity, " +
                "    INVENTORY.restock_quantity, " +
                "    INVENTORY.restock_date " +
                "FROM INVENTORY " +
                "JOIN PRODUCT ON INVENTORY.product_id = PRODUCT.product_id";


        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Create the Product object
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        null, // Assuming brand is null for now
                        null, // Assuming imageUrl is null for now
                        null  // Assuming category is null for now
                );

                // Create the InventoryProduct object
                InventoryProduct inventoryProduct = new InventoryProduct(
                        product,
                        rs.getInt("stock_quantity"),
                        rs.getInt("restock_quantity"),
                        rs.getString("restock_date")
                );

                inventoryList.add(inventoryProduct); // Add to the inventory list
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventoryList;
    }




    /**
     * Update the inventory after an order is processed by decreasing the quantity.
     *
     * @param productId    The ID of the product to update.
     * @param quantitySold The quantity sold to decrease from inventory.
     */
    public boolean updateInventory(int productId, int quantitySold) {
        String query = "UPDATE INVENTORY SET quantity = quantity - ? WHERE product_id = ?";

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
        String query = "SELECT product_id, quantity FROM ORDERITEM WHERE order_id = ?"; // Assuming ORDERITEM table exists

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
                "staffId=" + staffId +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }
}
