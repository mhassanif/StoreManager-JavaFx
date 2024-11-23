package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.items.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());




    // Method to create a new category
    public static int createCategory(Category category) {
        String sql = "INSERT INTO CATEGORY (name) VALUES (?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, category.getName());
            statement.executeUpdate();

            // Retrieve the generated category_id
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return category_id
                } else {
                    throw new SQLException("Failed to create category, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating category: {0}", e.getMessage());
        }
        return -1; // Return -1 if category creation fails
    }

    // Method to retrieve a category by its ID
    public static Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM CATEGORY WHERE category_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Category(
                            resultSet.getInt("category_id"),
                            resultSet.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving category by ID: {0}", e.getMessage());
        }
        return null; // Return null if category is not found
    }

    // Method to retrieve all categories
    public static List<Category> getAllCategories() {
        String sql = "SELECT * FROM CATEGORY";
        List<Category> categories = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getInt("category_id"),
                        resultSet.getString("name")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all categories: {0}", e.getMessage());
        }

        return categories;
    }

    // Method to update a category
    public static boolean updateCategory(Category category) {
        String sql = "UPDATE CATEGORY SET name = ? WHERE category_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, category.getName());
            statement.setInt(2, category.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category: {0}", e.getMessage());
        }
        return false;
    }

    // Method to delete a category by its ID
    public  static boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM CATEGORY WHERE category_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);
            int rowsAffected = statement.executeUpdate();

            // delete coresponding products if needed

            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category: {0}", e.getMessage());
        }
        return false;
    }
}