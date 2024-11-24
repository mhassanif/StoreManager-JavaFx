package com.storemanager.model.users;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.order.Order;
import com.storemanager.db.DBconnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.storemanager.dao.CustomerDAO;

import java.util.List;

/**
 * Represents a customer in the system.
 * Inherits from the User class and provides functionality specific to the customer role.
 */

public class Customer extends User {

    private int customerId;
    private ShoppingCart shoppingCart;
    private List<Order> orders;

    public Customer(int customerId, int userId, String username, String email, String password, String address, String phoneNumber) {
        super(userId, username, email, password, "Customer", address, phoneNumber);
        this.customerId = customerId;
        this.shoppingCart = CustomerDAO.initializeShoppingCart(customerId); // Initialize shopping cart
    }

    public int getCustomerId() {
        return customerId;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public void addToCart(CartItem cartItem) {
        shoppingCart.addItem(cartItem);
    }

    public void removeFromCart(CartItem cartItem) {
        shoppingCart.removeItem(cartItem);
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

    public void viewProfile() {
        System.out.println("Customer Profile:");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Username: " + getUsername());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + getAddress());
        System.out.println("Phone Number: " + getPhoneNumber());
    }

    public boolean updateCustomerInfo() {
        return CustomerDAO.updateCustomerInfo(this.getAddress(),this.getPhoneNumber(),this.customerId);
    }

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


