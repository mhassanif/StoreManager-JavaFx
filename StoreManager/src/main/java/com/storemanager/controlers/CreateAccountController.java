package com.storemanager.controlers;

import com.storemanager.db.DBconnector;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;

public class CreateAccountController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label messageLabel;

    /**
     * Initialize the ComboBox with role options.
     */
    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("customer", "warehousestaff"));
    }

    /**
     * Handle the Create Account button click.
     * This method validates the fields and inserts the data into the database.
     */
    public void handleCreateAccount() {
        // Get values from form fields
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String role = roleComboBox.getValue();

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        // Check if the role is valid (either Customer or WarehouseStaff)
        if (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("warehousestaff")) {
            messageLabel.setText("Invalid role selected. Only 'Customer' and 'WarehouseStaff' can be created.");
            return;
        }

        // Insert data into the USERS table
        String insertUserQuery = "INSERT INTO USERS (name, email, password, role, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, role);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, phone);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                // Get generated user_id
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);  // user_id is the first column
                    // Create user-specific data for either Customer or WarehouseStaff
                    if (role.equalsIgnoreCase("customer")) {
                        createCustomer(userId);
                    } else if (role.equalsIgnoreCase("warehousestaff")) {
                        createWarehouseStaff(userId);
                    }
                    messageLabel.setText("Account created successfully.");
                    System.out.println("Account created successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error creating account.");
        }
    }

    /**
     * Create a customer entry in the CUSTOMER table after creating a user.
     */
    private void createCustomer(int userId) {
        String insertCustomerQuery = "INSERT INTO CUSTOMER (user_id) VALUES (?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerQuery)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error creating customer.");
        }
    }

    /**
     * Create a warehouse staff entry in the STAFF table after creating a user.
     */
    private void createWarehouseStaff(int userId) {
        String insertStaffQuery = "INSERT INTO STAFF (user_id) VALUES (?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertStaffQuery)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error creating warehouse staff.");
        }
    }

    /**
     * Handle the Back to Login hyperlink click.
     */
    public void handleBackToLogin() {
        try {
            // Load the login screen again
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Login.fxml"));
            Parent root = loader.load();

            // Get the current stage (Window)
            Stage stage = (Stage) usernameField.getScene().getWindow();// Get the stage from the username's scene

            // Set the new scene for the stage
            Scene scene = new Scene(root, 980.0, 620.0);  // Maintain the same window size
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
