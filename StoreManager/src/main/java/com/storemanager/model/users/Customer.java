package com.storemanager.model.users;

import com.storemanager.db.DBconnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents a customer in the system.
 * Inherits from the User class and provides functionality specific to the customer role.
 */
public class Customer extends User {

    // Customer-specific fields can be added here
    private String address;
    private String phoneNumber;

    // Constructor
    public Customer(String username, String email, String password, String address, String phoneNumber) {
        super(username, email, password, "Customer"); // Assigning the role as "Customer"
        this.address = address;
        this.phoneNumber = phoneNumber;
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

