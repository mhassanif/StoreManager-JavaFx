package com.storemanager.dao;

import com.storemanager.db.DBconnector;
import com.storemanager.model.users.Customer;
import com.storemanager.model.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    /**
     * Fetches a Customer by their customer ID.
     *
     * @param customerId The customer ID.
     * @return Customer object or null if not found.
     */
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

    /**
     * Fetches a Customer by their user ID.
     *
     * @param userId The user ID.
     * @return Customer object or null if not found.
     */
    public static Customer getCustomerByUserId(int userId) {
        String query = "SELECT c.customer_id, u.user_id, u.name, u.email, u.password, u.role, u.address, u.phone " +
                "FROM CUSTOMER c " +
                "INNER JOIN USERS u ON c.user_id = u.user_id " +
                "WHERE u.user_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
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

    /**
     * Creates a new Customer in the database.
     *
     * @param customer The Customer object to be created.
     * @return true if successful, false otherwise.
     */
    public static boolean createCustomer(Customer customer) {
        // First, create a user using the UserDAO
        boolean userCreated = UserDAO.createUser(customer);
        if (!userCreated) {
            return false;
        }

        // Then, create the customer-specific record
        String query = "INSERT INTO CUSTOMER (user_id) VALUES (?)";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customer.getId());
            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates a Customer's information in the database.
     *
     * @param customer The Customer object with updated information.
     * @return true if successful, false otherwise.
     */
    public static boolean updateCustomer(Customer customer) {
        // Update the user using UserDAO
        boolean userUpdated = UserDAO.updateUser(customer);
        if (!userUpdated) {
            return false;
        }

        // Customer-specific updates (if any) can go here
        // Currently, there are no customer-specific fields in the CUSTOMER table to update
        return true;
    }

    /**
     * Deletes a Customer from the database.
     *
     * @param customerId The ID of the Customer to delete.
     * @return true if successful, false otherwise.
     */
    public static boolean deleteCustomer(int customerId) {
        // Delete the customer-specific record
        String query = "DELETE FROM CUSTOMER WHERE customer_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                // Optionally, delete the user using UserDAO
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
