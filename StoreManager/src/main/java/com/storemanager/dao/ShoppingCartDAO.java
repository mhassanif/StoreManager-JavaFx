package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.items.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShoppingCartDAO {
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartDAO.class.getName());

    // Method to synchronize shopping cart state with the database when adding an item
    public static boolean addOrUpdateItem(int cartId, CartItem cartItem) {
        // Check if the item already exists in the cart
        String checkSql = "SELECT quantity FROM CARTITEM WHERE cart_id = ? AND product_id = ?";
        String updateSql = "UPDATE CARTITEM SET quantity = ?, price = ? WHERE cart_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO CARTITEM (cart_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBconnector.getConnection()) {
            // Check if the product is already in the cart
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, cartId);
                checkStmt.setInt(2, cartItem.getProduct().getId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Product exists, update the quantity
                        int existingQuantity = rs.getInt("quantity");
                        int newQuantity = existingQuantity + cartItem.getQuantity();

                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, newQuantity);
                            updateStmt.setDouble(2, cartItem.calculateSubtotal());
                            updateStmt.setInt(3, cartId);
                            updateStmt.setInt(4, cartItem.getProduct().getId());
                            return updateStmt.executeUpdate() > 0;
                        }
                    }
                }
            }

            // Product does not exist, insert it
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setInt(1, cartId);
                insertStmt.setInt(2, cartItem.getProduct().getId());
                insertStmt.setInt(3, cartItem.getQuantity());
                insertStmt.setDouble(4, cartItem.calculateSubtotal());
                return insertStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding or updating item in cart: {0}", e.getMessage());
        }
        return false;
    }

    // Method to remove an item from a shopping cart
    public static boolean removeItem(int cartId, int productId) {
        String sql = "DELETE FROM CARTITEM WHERE cart_id = ? AND product_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);
            statement.setInt(2, productId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from cart: {0}", e.getMessage());
        }
        return false;
    }

    // Method to retrieve all items in a shopping cart
    public static List<CartItem> getItemsByCartId(int cartId) {
        String sql = "SELECT product_id, quantity FROM CARTITEM WHERE cart_id = ?";
        List<CartItem> items = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");

                    Product product = ProductDAO.getProductById(productId); // Fetch product details
                    if (product != null) {
                        items.add(new CartItem(product, quantity));
                    } else {
                        LOGGER.log(Level.WARNING, "Product with ID {0} not found", productId);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving cart items: {0}", e.getMessage());
        }
        return items;
    }

    // Method to clear all items from a shopping cart
    public static boolean clearCart(int cartId) {
        String sql = "DELETE FROM CARTITEM WHERE cart_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing cart: {0}", e.getMessage());
        }
        return false;
    }

    // Method to delete a shopping cart and its items
    public static boolean deleteCart(int cartId) {
        String deleteItemsSql = "DELETE FROM CARTITEM WHERE cart_id = ?";
        String deleteCartSql = "DELETE FROM SHOPPINGCART WHERE cart_id = ?";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            try (PreparedStatement deleteItemsStmt = connection.prepareStatement(deleteItemsSql)) {
                deleteItemsStmt.setInt(1, cartId);
                deleteItemsStmt.executeUpdate();
            }

            try (PreparedStatement deleteCartStmt = connection.prepareStatement(deleteCartSql)) {
                deleteCartStmt.setInt(1, cartId);
                deleteCartStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting cart: {0}", e.getMessage());
            try {
                DBconnector.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", rollbackEx.getMessage());
            }
        }
        return false;
    }

    // Method to get a shopping cart by customer ID
    public static ShoppingCart getShoppingCartByCustomerId(int customerId) {
        String getCartIdSql = "SELECT cart_id FROM SHOPPINGCART WHERE customer_id = ?";
        ShoppingCart shoppingCart = null;

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement getCartIdStmt = connection.prepareStatement(getCartIdSql)) {

            getCartIdStmt.setInt(1, customerId);

            try (ResultSet rs = getCartIdStmt.executeQuery()) {
                if (rs.next()) {
                    int cartId = rs.getInt("cart_id");

                    shoppingCart = new ShoppingCart(cartId);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving shopping cart for customer ID {0}: {1}",
                    new Object[]{customerId, e.getMessage()});
        }

        return shoppingCart;
    }
    public static boolean updateCartItem(int cartId, int productId, int quantity, double totalPrice) {
        String sql = "UPDATE CARTITEM SET quantity = ?, price = ? WHERE cart_id = ? AND product_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setDouble(2, totalPrice);
            ps.setInt(3, cartId);
            ps.setInt(4, productId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
