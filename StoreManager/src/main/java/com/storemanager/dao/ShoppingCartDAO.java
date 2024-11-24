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

    // Method to retrieve all shopping carts with their items
    public static List<ShoppingCart> getAllShoppingCarts() {
        String cartSql = "SELECT DISTINCT cart_id, customer_id FROM SHOPPINGCART";
        String itemsSql = "SELECT product_id, quantity FROM CARTITEM WHERE cart_id = ?";
        List<ShoppingCart> shoppingCarts = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement cartStmt = connection.prepareStatement(cartSql)) {

            try (ResultSet cartRs = cartStmt.executeQuery()) {
                while (cartRs.next()) {
                    int cartId = cartRs.getInt("cart_id");
                    int customerId = cartRs.getInt("customer_id");

                    ShoppingCart shoppingCart = new ShoppingCart(cartId);

                    // Retrieve items for this cart
                    try (PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {
                        itemsStmt.setInt(1, cartId);
                        try (ResultSet itemsRs = itemsStmt.executeQuery()) {
                            while (itemsRs.next()) {
                                int productId = itemsRs.getInt("product_id");
                                int quantity = itemsRs.getInt("quantity");

                                // Use ProductDAO to fetch product details
                                Product product = ProductDAO.getProductById(productId);
                                if (product != null) {
                                    CartItem cartItem = new CartItem(product, quantity);
                                    shoppingCart.addItem(cartItem);
                                } else {
                                    LOGGER.log(Level.WARNING, "Product with ID {0} not found", productId);
                                }
                            }
                        }
                    }

                    shoppingCarts.add(shoppingCart);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all shopping carts: {0}", e.getMessage());
        }

        return shoppingCarts;
    }

    // Method to create a new shopping cart for a customer
    public static int createShoppingCart(int customerId) {
        String sql = "INSERT INTO SHOPPINGCART (customer_id) VALUES (?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, customerId);
            statement.executeUpdate();

            // Retrieve the generated cart_id
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return cart_id
                } else {
                    throw new SQLException("Failed to create shopping cart, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating shopping cart: {0}", e.getMessage());
        }
        return -1; // Return -1 if cart creation fails
    }

    // Method to retrieve a shopping cart by its ID
    public static ShoppingCart getShoppingCartById(int cartId) {
        String sql = "SELECT cart_item_id, product_id, quantity FROM CARTITEM WHERE cart_id = ?";
        ShoppingCart shoppingCart = new ShoppingCart(cartId);

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Fetch product details using ProductDAO
                    Product product = ProductDAO.getProductById(resultSet.getInt("product_id"));

                    if (product != null) {
                        // Create CartItem and add it to the shopping cart
                        CartItem cartItem = new CartItem(product, resultSet.getInt("quantity"));
                        shoppingCart.addItem(cartItem);
                    } else {
                        LOGGER.log(Level.WARNING, "Product with ID {0} not found", resultSet.getInt("product_id"));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving shopping cart: {0}", e.getMessage());
        }

        return shoppingCart;
    }

    // Method to delete a shopping cart and its associated cart items for a specific customer
    public static boolean deleteShoppingCartByCustomer(int customerId) {
        String deleteCartItemsSql = "DELETE FROM CARTITEM WHERE cart_id IN (SELECT cart_id FROM SHOPPINGCART WHERE customer_id = ?)";
        String deleteCartSql = "DELETE FROM SHOPPINGCART WHERE customer_id = ?";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            // Step 1: Delete all cart items associated with the shopping cart
            try (PreparedStatement cartItemsStmt = connection.prepareStatement(deleteCartItemsSql)) {
                cartItemsStmt.setInt(1, customerId);
                cartItemsStmt.executeUpdate();
            }

            // Step 2: Delete the shopping cart
            try (PreparedStatement cartStmt = connection.prepareStatement(deleteCartSql)) {
                cartStmt.setInt(1, customerId);
                cartStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            LOGGER.log(Level.INFO, "Deleted shopping cart and associated items for customer {0}", customerId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting shopping cart for customer {0}: {1}", new Object[]{customerId, e.getMessage()});
            try {
                // Rollback transaction in case of error
                DBconnector.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", rollbackEx.getMessage());
            }
        }

        return false;
    }
}
