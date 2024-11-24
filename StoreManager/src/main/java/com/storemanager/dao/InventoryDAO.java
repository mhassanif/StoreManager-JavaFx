package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.items.InventoryProduct;
import com.storemanager.model.items.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryDAO {
    private static final Logger LOGGER = Logger.getLogger(InventoryDAO.class.getName());

    // Method to get all inventory products
    public List<InventoryProduct> getAllInventory() {
        String sql = "SELECT * FROM INVENTORY";
        List<InventoryProduct> inventoryList = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                Product product = ProductDAO.getProductById(productId); // Get product details using ProductDAO
                if (product != null) {
                    int stockQuantity = resultSet.getInt("stock_quantity");
                    int restockQuantity = resultSet.getInt("restock_quantity");
                    String restockDate = resultSet.getString("restock_date");

                    // Create InventoryProduct object and add it to the list
                    InventoryProduct inventoryProduct = new InventoryProduct(product, stockQuantity, restockQuantity, restockDate);
                    inventoryList.add(inventoryProduct);
                } else {
                    LOGGER.log(Level.WARNING, "Product with ID {0} not found in inventory", productId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all inventory products: {0}", e.getMessage());
        }

        return inventoryList;
    }



    // Method to get inventory details by product ID
    public InventoryProduct getInventoryByProductId(int productId) {
        String sql = "SELECT * FROM INVENTORY WHERE product_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Product product = ProductDAO.getProductById(productId); // Get product details
                    int stockQuantity = resultSet.getInt("stock_quantity");
                    int restockQuantity = resultSet.getInt("restock_quantity");
                    String restockDate = resultSet.getString("restock_date");
                    return new InventoryProduct(product, stockQuantity, restockQuantity, restockDate);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving inventory by product ID: {0}", e.getMessage());
        }
        return null; // Return null if no inventory found for the given product ID
    }

    // Method to update stock quantity for a product
    public boolean updateStockQuantity(int productId, int quantity) {
        String sql = "UPDATE INVENTORY SET stock_quantity = ? WHERE product_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock quantity: {0}", e.getMessage());
        }
        return false;
    }

    // Method to restock a product by adding to the existing stock quantity
    public boolean restockProduct(int productId, int quantity) {
        String sql = "UPDATE INVENTORY SET stock_quantity = stock_quantity + ?, restock_date = GETDATE() WHERE product_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error restocking product: {0}", e.getMessage());
        }
        return false;
    }

    // Method to get products with low stock based on a threshold
    public List<InventoryProduct> getLowStockProducts(int threshold) {
        String sql = "SELECT * FROM INVENTORY WHERE stock_quantity <= ?";
        List<InventoryProduct> lowStockProducts = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, threshold);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    Product product = ProductDAO.getProductById(productId); // Get product details
                    int stockQuantity = resultSet.getInt("stock_quantity");
                    int restockQuantity = resultSet.getInt("restock_quantity");
                    String restockDate = resultSet.getString("restock_date");

                    lowStockProducts.add(new InventoryProduct(product, stockQuantity, restockQuantity, restockDate));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving low stock products: {0}", e.getMessage());
        }
        return lowStockProducts;
    }
    /**
     * Sets the restock level for a given product in the inventory.
     *
     * @param productId The ID of the product
     * @param restockLevel The new restock level to set
     * @return boolean true if the update was successful, false otherwise
     */
    public static boolean setRestockLevel(int productId, int restockLevel) {
        String updateQuery = "UPDATE Inventory SET restock_level = ? WHERE product_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setInt(1, restockLevel);
            preparedStatement.setInt(2, productId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;  // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false in case of an error
        }
    }
}
