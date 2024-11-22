package com.storemanager;

import com.storemanager.db.DBconnector;
import com.storemanager.auth.CurrentUser;
import com.storemanager.model.users.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import javafx.scene.Parent;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    /**
     * Handle the login process.
     */
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            System.out.println("Empty username or password.");
            return;
        }

        // Authenticate user against the database
        if (authenticateUser(username, password)) {
            messageLabel.setText("Login successful!");
            System.out.println("Login successful!");
        } else {
            messageLabel.setText("Invalid credentials");
            System.out.println("Invalid credentials");
        }
    }

    /**
     * Authenticate the user using the database.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if the credentials are valid, false otherwise.
     */
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM USERS WHERE name = ? AND password = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Extract user information
                int userId = resultSet.getInt("user_id");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");

                // Check and set the user object based on role
                if (role.equalsIgnoreCase("customer")) {
                    setCurrentCustomer(userId, email, username, password, address, phone);
                } else {
                    setCurrentStaff(userId, email, username, password, address, phone, role);
                }

                loadDashboard(role);  // Load the appropriate dashboard
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error connecting to the database.");
        }
        return false;
    }

    /**
     * Set the current user as a Customer.
     */
    private void setCurrentCustomer(int userId, String email, String username, String password, String address, String phone) {
        String query = "SELECT * FROM CUSTOMER WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                CurrentUser.getInstance().setUser(new Customer(customerId, userId, username, email, password, address, phone));
                System.out.println("Logged in as Customer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error retrieving customer data.");
        }
    }

    /**
     * Set the current user as a Staff member.
     */
    private void setCurrentStaff(int userId, String email, String username, String password, String address, String phone, String role) {
        String query = "SELECT * FROM STAFF WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int staffId = resultSet.getInt("staff_id");

                switch (role.toLowerCase()) {
                    case "admin":
                        CurrentUser.getInstance().setUser(new Admin(staffId, userId, username, email, password, address, phone));
                        System.out.println("Logged in as Admin");
                        break;
                    case "manager":
                        CurrentUser.getInstance().setUser(new Manager(staffId, userId, username, email, password, address, phone));
                        System.out.println("Logged in as Manager");
                        break;
                    case "warehousestaff":
                        CurrentUser.getInstance().setUser(new WarehouseStaff(staffId, userId, username, email, password, address, phone));
                        System.out.println("Logged in as Warehouse Staff");
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown role: " + role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error retrieving staff data.");
        }
    }

    /**
     * Load the appropriate dashboard based on the user role.
     */
    private void loadDashboard(String role) {
        try {
            FXMLLoader loader;
            Parent root;

            // Load the correct dashboard based on the role
            switch (role.toLowerCase()) {
                case "admin":
                    loader = new FXMLLoader(getClass().getResource("/com/storemanager/AdminDashboard.fxml"));
                    break;
                case "manager":
                    loader = new FXMLLoader(getClass().getResource("/com/storemanager/ManagerDashboard.fxml"));
                    break;
                case "customer":
                    loader = new FXMLLoader(getClass().getResource("/com/storemanager/CustomerDashboard.fxml"));
                    break;
                case "warehousestaff":
                    loader = new FXMLLoader(getClass().getResource("/com/storemanager/WarehouseStaffDashboard.fxml"));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + role);
            }

            root = loader.load();

            // Get current stage and set new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(role + " Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load dashboard.");
        }
    }

    /**
     * Handle the action for creating a new account.
     */
    @FXML
    private void hlCreateAnAccount() {
        System.out.println("Create an Account clicked");
        // You can load the account creation scene or logic here
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/CreateAccount.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Create Account");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load account creation page.");
        }
    }

    /**
     * Handle the database setup action.
     */
    @FXML
    private void hlDbOnAction() {
        System.out.println("Database Setup clicked");
/*        // Logic to set up or reset the database (or navigate to a setup page)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/DatabaseSetup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Database Setup");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load database setup page.");
        }*/
    }
}
