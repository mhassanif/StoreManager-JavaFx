package com.storemanager.dao;

import com.storemanager.db.DBconnector;
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
        if (!UserDAO.createUser(customer)) {
            return false;
        }

        String query = "INSERT INTO CUSTOMER (user_id) VALUES (?)";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customer.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateCustomer(Customer customer) {
        return UserDAO.updateUser(customer);
    }

    // Delete customer by user_id
    public static boolean deleteCustomerByUserId(int userId) {
        // First, get the customer_id using the user_id
        Customer customer = CustomerDAO.getCustomerByUserId(userId);
        if (customer == null) return false;

        int customerId = customer.getId();

        // Call the deleteCustomer method to handle the deletion
        return deleteCustomer(customerId);
    }

    // Delete customer by customer_id
    public static boolean deleteCustomer(int customerId) {
        Customer customer = getCustomerById(customerId);
        if (customer == null) return false;

        int userId = customer.getId();

        try {
            // Delete related records using appropriate DAOs
            NotificationDAO.deleteNotificationsByUserId(userId);
            FeedbackDAO.deleteFeedbackByCustomer(customerId);
            ShoppingCartDAO.deleteShoppingCartByCustomer(customerId);
            OrderDAO.deleteOrderByCustomer(customerId);

            // Delete customer record
            String query = "DELETE FROM CUSTOMER WHERE customer_id = ?";
            try (Connection connection = DBconnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, customerId);
                if (preparedStatement.executeUpdate() > 0) {
                    // Delete user record as well
                    return UserDAO.deleteUser(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
