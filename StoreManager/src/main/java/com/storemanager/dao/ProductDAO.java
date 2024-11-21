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

    // Method to find a product by its ID
    public Product findById(int id) {
        String query = "SELECT p.*, c.name AS category_name FROM PRODUCT p " +
                "JOIN CATEGORY c ON p.category_id = c.category_id WHERE p.product_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding product by ID: {0}", e.getMessage());
        }
        return null;
    }

    // Method to retrieve all products
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name AS category_name FROM PRODUCT p " +
                "JOIN CATEGORY c ON p.category_id = c.category_id";

        try (Connection conn = DBconnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all products: {0}", e.getMessage());
        }
        return products;
    }

    // Method to save a new product
    public void save(Product product) {
        String query = "INSERT INTO PRODUCT (name, brand, description, price, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getBrand());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getCategory().getId());
            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Product saved successfully: {0}", product.getName());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving product: {0}", e.getMessage());
        }
    }

    // Method to update an existing product
    public void update(Product product) {
        String query = "UPDATE PRODUCT SET name = ?, brand = ?, description = ?, price = ?, category_id = ? WHERE product_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getBrand());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getCategory().getId());
            pstmt.setInt(6, product.getId());
            pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Product updated successfully: {0}", product.getName());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product: {0}", e.getMessage());
        }
    }

    // Method to delete a product by its ID
    public void delete(int id) {
        String query = "DELETE FROM PRODUCT WHERE product_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Product deleted successfully with ID: {0}", id);
            } else {
                LOGGER.log(Level.WARNING, "No product found with ID: {0}", id);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product: {0}", e.getMessage());
        }
    }

    // Utility function retrived row to object
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
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
