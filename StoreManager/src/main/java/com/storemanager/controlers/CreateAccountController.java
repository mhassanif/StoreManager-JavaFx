package com.storemanager.controlers;

import com.storemanager.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.collections.FXCollections;
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
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        if (!role.equalsIgnoreCase("customer") && !role.equalsIgnoreCase("warehousestaff")) {
            messageLabel.setText("Invalid role selected. Only 'Customer' and 'WarehouseStaff' can be created.");
            return;
        }

        // Use overloaded createUser method from UserDAO to create user
        boolean isUserCreated = false;
        if (role.equalsIgnoreCase("customer")) {
            isUserCreated = UserDAO.createUser(username, email, password, "Customer", address, phone);  // 'Customer' role
        } else if (role.equalsIgnoreCase("warehousestaff")) {
            isUserCreated = UserDAO.createUser(username, email, password, "Staff", address, phone);  // 'Staff' role with position handled by DAO
        }

        if (isUserCreated) {
            messageLabel.setText("Account created successfully.");
        } else {
            messageLabel.setText("Error creating account.");
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
            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the stage from the username's scene

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
