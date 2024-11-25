package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.users.Customer;
import com.storemanager.model.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        String query = "SELECT c.customer_id, u.user_id, u.name, u.email, u.password, u.role, u.address, u.phone " +
                "FROM CUSTOMER c " +
                "INNER JOIN USERS u ON c.user_id = u.user_id";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                customers.add(new Customer(
                        resultSet.getInt("customer_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("address"),
                        resultSet.getString("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    public static Customer getCustomerById(int customerId) {
        String query = "SELECT c.customer_id, u.user_id, u.name, u.email, u.password, u.role, u.address, u.phone " +
                "FROM CUSTOMER c " +
                "INNER JOIN USERS u ON c.user_id = u.user_id " +
                "WHERE c.customer_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getInt("customer_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("address"),
                            resultSet.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Customer getCustomerByUserId(int userId) {
        String query = "SELECT * FROM CUSTOMER WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                // Fetch user details
                User user = UserDAO.getUserById(userId);
                return new Customer(customerId, user.getId(), user.getUsername(), user.getEmail(),
                        user.getPassword(), user.getAddress(), user.getPhoneNumber());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no customer found
    }

    public static boolean createCustomer(Customer customer) {
        String query = "INSERT INTO CUSTOMER (user_id) VALUES (?)";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, customer.getId());
            if (preparedStatement.executeUpdate() > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        customer.setId(customerId);
                        customer.setShoppingCart(initializeShoppingCart(customerId)); // Create and assign cart
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createCustomer(int userId) {
        String query = "INSERT INTO CUSTOMER (user_id) VALUES (?)";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, userId);
            if (preparedStatement.executeUpdate() > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        initializeShoppingCart(customerId); // Create cart during customer creation
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ShoppingCart initializeShoppingCart(int customerId) {
        try (Connection connection = DBconnector.getConnection()) {
            // Step 1: Check if a shopping cart already exists for the customer
            String query = "SELECT cart_id FROM SHOPPINGCART WHERE customer_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, customerId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int cartId = resultSet.getInt("cart_id");
                        return new ShoppingCart(cartId); // Return the cart with the retrieved ID
                    } else {
                        // Step 2: If no cart exists, create one for the customer
                        String insertQuery = "INSERT INTO SHOPPINGCART (customer_id) VALUES (?)";
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
            e.printStackTrace(); // Log the exception for debugging
        }

        return null; // Return null if an error occurs
    }

    public static boolean updateCustomer(Customer customer) {
        return UserDAO.updateUser(customer);
    }

    public static boolean deleteCustomerByUserId(int userId) {
        Customer customer = CustomerDAO.getCustomerByUserId(userId);
        if (customer == null) return false;

        int customerId = customer.getCustomerId();
        return deleteCustomer(customerId);
    }

    public static boolean deleteCustomer(int customerId) {
        Customer customer = getCustomerById(customerId);
        if (customer == null) return false;

        int userId = customer.getId();

        try {
            NotificationDAO.deleteNotificationsByUserId(userId);
            FeedbackDAO.deleteFeedbackByCustomer(customerId);
            ShoppingCartDAO.deleteCart(customer.getShoppingCart().getCartId());
            OrderDAO.deleteOrderByCustomer(customerId);

            String query = "DELETE FROM CUSTOMER WHERE customer_id = ?";
            try (Connection connection = DBconnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, customerId);
                if (preparedStatement.executeUpdate() > 0) {
                    return UserDAO.deleteUser(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateCustomerInfo(String address, String phone, int id) {
        try (Connection connection = DBconnector.getConnection()) {
            String query = "UPDATE USERS SET address = ?, phone_number = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, address);
                preparedStatement.setString(2, phone);
                preparedStatement.setInt(3, id); // Use user ID here

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
