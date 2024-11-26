package com.storemanager.model.users;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.order.Order;
import com.storemanager.dao.CustomerDAO;

import java.util.List;

/**
 * Represents a customer in the system.
 * Inherits from the User class and provides functionality specific to the customer role.
 */
public class Customer extends User {

    private int customerId;
    private double balance;

    private ShoppingCart shoppingCart;
    private List<Order> orders;

    // Constructor
    // Existing constructor with balance parameter
    public Customer(int customerId, int userId, String username, String email, String password, String address, String phoneNumber, double balance) {
        super(userId, username, email, password, "Customer", address, phoneNumber);
        this.customerId = customerId;
        this.balance = balance;  // Set the balance from the constructor parameter
        this.shoppingCart = CustomerDAO.initializeShoppingCart(customerId); // Initialize shopping cart
    }

    // Existing constructor without balance (balance will be fetched later)
    public Customer(int customerId, int userId, String username, String email, String password, String address, String phoneNumber) {
        super(userId, username, email, password, "Customer", address, phoneNumber);
        this.customerId = customerId;
        this.shoppingCart = CustomerDAO.initializeShoppingCart(customerId);
        this.balance = CustomerDAO.fetchCustomerBalance(customerId);
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double newBalance) {
        boolean success=CustomerDAO.setCustomerBalance(this.getCustomerId(),newBalance);
        if(success){
            this.balance = newBalance;
        }
    }

    public void recharge(double amount) {
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Recharge amount must be positive.");
            }
            setBalance(balance + amount);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage()); // Log the error message
            // Optionally, you could log it to a file or take other actions here
        }
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

    // Cart management
    public void addToCart(CartItem cartItem) {
        shoppingCart.addItem(cartItem);
    }

    public void removeFromCart(CartItem cartItem) {
        shoppingCart.removeItem(cartItem);
    }

    @Override
    public void logout() {
        System.out.println("Customer " + getUsername() + " has logged out.");
    }

    public void viewProfile() {
        System.out.println("Customer Profile:");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + getAddress());
        System.out.println("Phone Number: " + getPhoneNumber());
        System.out.println("Balance: $" + balance);
    }

    // Update customer information
    public boolean updateCustomerInfo() {
        return CustomerDAO.updateCustomerInfo(this.getAddress(), this.getPhoneNumber(), this.customerId, this.balance);
    }

    // Static utility methods
    public static Customer getCustomerById(int customerId) {
        return CustomerDAO.getCustomerById(customerId);
    }

    public static Customer getCustomerByUserId(int userId) {
        return CustomerDAO.getCustomerByUserId(userId);
    }

    public static boolean createCustomer(Customer customer) {
        return CustomerDAO.createCustomer(customer);
    }

    public static boolean createCustomer(int userId) {
        return CustomerDAO.createCustomer(userId);
    }

    public static boolean updateCustomer(Customer customer) {
        return CustomerDAO.updateCustomer(customer);
    }

    public static boolean deleteCustomerByUserId(int userId) {
        return CustomerDAO.deleteCustomerByUserId(userId);
    }

    public static boolean deleteCustomer(int customerId) {
        return CustomerDAO.deleteCustomer(customerId);
    }

    public static List<Customer> getAllCustomers() {
        return CustomerDAO.getAllCustomers();
    }
}
