package com.storemanager.model.users;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.db.DBconnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Represents a customer in the system.
 * Inherits from the User class and provides functionality specific to the customer role.
 */
public class Customer extends User {

    private ShoppingCart shoppingCart; // Customer's shopping cart
    private List<Order> orders;        // List of customer's past orders

    // Constructor
    public Customer(int id, String username, String email, String password, String address, String phoneNumber) {
        super(id, username, email, password, "Customer", address, phoneNumber); // Assigning the role as "Customer"
        this.shoppingCart = initializeShoppingCart(id); // Initialize the shopping cart with cartId
    }

    // Getters and Setters
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void logout() {
        System.out.println("Customer " + getUsername() + " has logged out.");
    }

    public boolean updateCustomerInfo() {
        try (Connection connection = DBconnector.getConnection()) {
            String query = "UPDATE users SET address = ?, phone_number = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, this.getAddress());
                preparedStatement.setString(2, this.getPhoneNumber());
                preparedStatement.setString(3, getUsername());

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewProfile() {
        System.out.println("Customer Profile:");
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + this.getAddress());
        System.out.println("Phone Number: " + this.getPhoneNumber());
    }

    public void addToCart(CartItem cartItem) {
        shoppingCart.addItem(cartItem);
    }

    public void removeFromCart(CartItem cartItem) {
        shoppingCart.removeItem(cartItem);
    }

    public Order placeOrder() {
        if (shoppingCart.getItems().isEmpty()) {
            System.out.println("Cannot place order. Shopping cart is empty.");
            return null;
        }

        Order newOrder = shoppingCart.checkout(this);

        if (newOrder != null) {
            orders.add(newOrder);
            return newOrder;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }

    /**
     * Initializes the customer's shopping cart by retrieving or creating a cartId in the database.
     *
     * @param customerId The ID of the customer.
     * @return A ShoppingCart object with a valid cartId.
     */
    private ShoppingCart initializeShoppingCart(int customerId) {
        try (Connection connection = DBconnector.getConnection()) {
            String query = "SELECT cart_id FROM shopping_carts WHERE customer_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customerId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int cartId = resultSet.getInt("cart_id");
                        return new ShoppingCart(cartId); // Return the cart with the retrieved ID
                    } else {
                        // If no cart exists, create one
                        String insertQuery = "INSERT INTO shopping_carts (customer_id) VALUES (?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            insertStatement.setInt(1, customerId);
                            insertStatement.executeUpdate();
                            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    int newCartId = generatedKeys.getInt(1);
                                    return new ShoppingCart(newCartId); // Return the cart with the new ID
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if an error occurs
    }
}
