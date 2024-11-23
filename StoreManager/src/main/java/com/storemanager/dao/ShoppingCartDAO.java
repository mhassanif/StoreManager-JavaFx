package com.storemanager.dao;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.items.Product;
import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShoppingCartDAO {
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartDAO.class.getName());

    // Method to retrieve all shopping carts with their items
    public List<ShoppingCart> getAllShoppingCarts() {
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
    public int createShoppingCart(int customerId) {
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
    public ShoppingCart getShoppingCartById(int cartId) {
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

    // Method to add an item to the shopping cart
    public void addItemToCart(int cartId, CartItem cartItem) {
        String sql = "MERGE INTO CARTITEM AS target " +
                "USING (SELECT ? AS cart_id, ? AS product_id) AS source " +
                "ON target.cart_id = source.cart_id AND target.product_id = source.product_id " +
                "WHEN MATCHED THEN " +
                "UPDATE SET quantity = target.quantity + ?, price = ? " +
                "WHEN NOT MATCHED THEN " +
                "INSERT (cart_id, product_id, quantity, price) VALUES (?, ?, ?, ?);";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            double totalPrice = product.getPrice() * quantity;

            // Set parameters
            statement.setInt(1, cartId);
            statement.setInt(2, product.getId());
            statement.setInt(3, quantity);
            statement.setDouble(4, totalPrice);
            statement.setInt(5, cartId);
            statement.setInt(6, product.getId());
            statement.setInt(7, quantity);
            statement.setDouble(8, totalPrice);

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding item to cart: {0}", e.getMessage());
        }
    }

    // Method to remove an item from the shopping cart
    public void removeItemFromCart(int cartId, int productId) {
        String sql = "DELETE FROM CARTITEM WHERE cart_id = ? AND product_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);
            statement.setInt(2, productId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Removed product {0} from cart {1}", new Object[]{productId, cartId});
            } else {
                LOGGER.log(Level.WARNING, "No product {0} found in cart {1}", new Object[]{productId, cartId});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from cart: {0}", e.getMessage());
        }
    }

    // Method to clear all items from a shopping cart
    public void clearCart(int cartId) {
        String sql = "DELETE FROM CARTITEM WHERE cart_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);
            statement.executeUpdate();
            LOGGER.log(Level.INFO, "Cleared all items from cart {0}", cartId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing cart: {0}", e.getMessage());
        }
    }

    // Method to get the total price of all items in the cart
    public double getCartTotalPrice(int cartId) {
        String sql = "SELECT SUM(quantity * price) AS total_price FROM CARTITEM WHERE cart_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cartId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("total_price");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating total price: {0}", e.getMessage());
        }

        return 0.0; // Default to 0 if no items are found
    }
}