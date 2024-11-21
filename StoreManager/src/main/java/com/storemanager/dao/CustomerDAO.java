package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.order.Order;
import com.storemanager.model.users.Customer;
import com.storemanager.model.cart.ShoppingCart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public Customer read(int customerID) {
        Customer customer = null;

        // SQL query to join USERS table and fetch customer details
        String customerQuery = "SELECT u.user_id, u.name, u.email, u.password, u.address, u.phone " +
                "FROM USERS u " +
                "INNER JOIN CUSTOMER c ON u.user_id = c.user_id " +
                "WHERE c.customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(customerQuery)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            // If the result set contains a record, process it
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phone");

//                // Instantiate the Customer object
                customer = new Customer(userId, username, email, password, address, phoneNumber);
//
//                // Initialize the shopping cart with the cartId from the database
//                customer.setShoppingCart(new ShoppingCart(cartId));
//
//                // You could also load the customer's past orders if needed
                // customer.setOrders(loadOrders(customerID)); // Implement the order loading logic as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;  // Return the Customer object (if found), otherwise null
    }
}
