package com.storemanager.dao;

import com.storemanager.model.items.Product;
import com.storemanager.model.items.Category;
import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());

    // Method to get all products
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name AS category_name FROM PRODUCT p " +
                "JOIN CATEGORY c ON p.category_id = c.category_id";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all products: {0}", e.getMessage());
        }
        return products;
    }


    // Method to get a product by its ID
    public static Product getProductById(int productId) {
        String query = "SELECT p.*, c.name AS category_name FROM PRODUCT p " +
                "JOIN CATEGORY c ON p.category_id = c.category_id WHERE p.product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting product by ID: {0}", e.getMessage());
        }
        return null;
    }

    // Method to get products by category ID
    public  static List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name AS category_name FROM PRODUCT p " +
                "JOIN CATEGORY c ON p.category_id = c.category_id WHERE c.category_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting products by category ID: {0}", e.getMessage());
        }
        return products;
    }

    // Method to search products by a keyword
    public  static  List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name AS category_name FROM PRODUCT p " +
                "JOIN CATEGORY c ON p.category_id = c.category_id " +
                "WHERE p.name LIKE ? OR p.description LIKE ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRowToProduct(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products: {0}", e.getMessage());
        }
        return products;
    }

    // Method to create a new product
    public  static boolean createProduct(Product product) {
        String query = "INSERT INTO PRODUCT (name, brand, description, price, category_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getBrand());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getCategory().getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating product: {0}", e.getMessage());
        }
        return false;
    }

    // Method to update an existing product
    public  static boolean updateProduct(Product product) {
        String query = "UPDATE PRODUCT SET name = ?, brand = ?, description = ?, price = ?, category_id = ? WHERE product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getBrand());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getCategory().getId());
            pstmt.setInt(6, product.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product: {0}", e.getMessage());
        }
        return false;
    }

    // Method to delete a product by its ID
    public static boolean deleteProduct(int productId) {
        // Step 1: Delete all related CartItem entries
        String deleteCartItemQuery = "DELETE FROM CARTITEM WHERE product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteCartItemQuery)) {

            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting related cart items for product {0}: {1}", new Object[]{productId, e.getMessage()});
            return false; // If deletion of cart items fails, stop further execution
        }

        // Step 2: Delete all related OrderItem entries
        String deleteOrderItemQuery = "DELETE FROM ORDERITEM WHERE product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteOrderItemQuery)) {

            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting related order items for product {0}: {1}", new Object[]{productId, e.getMessage()});
            return false; // If deletion of order items fails, stop further execution
        }

        // Step 3: Delete all related Inventory entries (inventory depends on product)
        String deleteInventoryQuery = "DELETE FROM INVENTORY WHERE product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteInventoryQuery)) {

            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting related inventory for product {0}: {1}", new Object[]{productId, e.getMessage()});
            return false; // If deletion of inventory entries fails, stop further execution
        }

        // Step 4: Delete the product itself
        String deleteProductQuery = "DELETE FROM PRODUCT WHERE product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteProductQuery)) {

            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product {0}: {1}", new Object[]{productId, e.getMessage()});
        }
        return false;
    }



    // Utility function to map a result set row to a Product object
    private  static  Product mapRowToProduct(ResultSet rs) throws SQLException {
        int categoryId = rs.getInt("category_id");
        String categoryName = rs.getString("category_name");

        return new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("brand"),
                null, // Assuming no image URL for now
                new Category(categoryId, categoryName),
                rs.getString("description")
        );
    }
}