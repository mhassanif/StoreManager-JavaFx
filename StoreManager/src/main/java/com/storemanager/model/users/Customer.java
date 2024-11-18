package com.storemanager.model.users;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.db.DBconnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Represents a customer in the system.
 * Inherits from the User class and provides functionality specific to the customer role.
 */
public class Customer extends User {

    // Customer-specific fields
    private String address;
    private String phoneNumber;
    private ShoppingCart shoppingCart; // Customer's shopping cart
    private List<Order> orders; // List of customer's past orders

    // Constructor
    public Customer(String username, String email, String password, String address, String phoneNumber) {
        super(username, email, password, "Customer"); // Assigning the role as "Customer"
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.shoppingCart = new ShoppingCart(); // Initialize the shopping cart
    }

    // Getters and Setters for customer-specific fields
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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

    /**
     * Override the logout method for the customer.
     * Currently, it could be a simple message or session handling logic.
     */
    @Override
    public void logout() {
        // Implement any session-related logout functionality
        System.out.println("Customer " + getUsername() + " has logged out.");
    }

    /**
     * Method to update the customer's information in the database.
     * For example, address or phone number can be updated.
     */
    public boolean updateCustomerInfo() {
        try (Connection connection = DBconnector.getConnection()) {

            String query = "UPDATE users SET address = ?, phone_number = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, this.address);
                preparedStatement.setString(2, this.phoneNumber);
                preparedStatement.setString(3, getUsername());

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Return true if update is successful
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }

    /**
     * Method to view the customer's profile information.
     * This could display details like username, email, address, and phone number.
     */
    public void viewProfile() {
        System.out.println("Customer Profile:");
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + this.address);
        System.out.println("Phone Number: " + this.phoneNumber);
    }

    /**
     * Method to add an item to the shopping cart.
     * @param cartItem The item to be added to the cart.
     */
    public void addToCart(CartItem cartItem) {
        shoppingCart.addItem(cartItem); // Adds item to the shopping cart
    }

    /**
     * Method to remove an item from the shopping cart.
     * @param cartItem The item to be removed from the cart.
     */
    public void removeFromCart(CartItem cartItem) {
        shoppingCart.removeItem(cartItem); // Removes item from the shopping cart
    }

    /**
     * Method to place an order using the items in the shopping cart.
     * The checkout method of the shopping cart is used to create the order.
     * @return The newly created order, or null if the cart is empty.
     */
    public Order placeOrder() {
        if (shoppingCart.getItems().isEmpty()) {
            System.out.println("Cannot place order. Shopping cart is empty.");
            return null;
        }

        // Checkout the cart and create a new order
        Order newOrder = shoppingCart.checkout(this);

        if (newOrder != null) {
            orders.add(newOrder); // Add the new order to the customer's order history
            return newOrder;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
