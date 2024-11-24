package com.storemanager.controlers;

import com.storemanager.db.DBconnector;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerProfileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    private String username; // Passed from DashboardController
    private String password; // Passed from DashboardController

    /**
     * Sets the login credentials and loads the user profile.
     */
    public void setLoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        System.out.println("setLoginCredentials called in CustomerProfileController with username: " + username);
        loadUserProfile();
    }

    /**
     * Loads the user's profile details from the database.
     */
    private void loadUserProfile() {
        String query = "SELECT * FROM USERS WHERE name = ? AND password = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            System.out.println("Executing query to load profile: " + query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Debugging: Print fetched values
                System.out.println("Fetched Username: " + resultSet.getString("name"));
                System.out.println("Fetched Email: " + resultSet.getString("email"));
                System.out.println("Fetched Address: " + resultSet.getString("address"));
                System.out.println("Fetched Phone: " + resultSet.getString("phone"));

                // Populate the labels and text fields
                usernameLabel.setText(resultSet.getString("name"));
                emailLabel.setText(resultSet.getString("email"));
                addressField.setText(resultSet.getString("address"));
                phoneField.setText(resultSet.getString("phone"));
                passwordField.setText(resultSet.getString("password")); // Prepopulate the password
            } else {
                System.err.println("User not found for the provided username and password.");
                showAlert(AlertType.ERROR, "Error", "Unable to load user profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred while loading the profile.");
        }
    }

    /**
     * Handles the "Save Changes" button action to update the user's profile.
     */
    @FXML
    private void handleSaveChanges() {
        String updatedAddress = addressField.getText();
        String updatedPhone = phoneField.getText();
        String updatedPassword = passwordField.getText();

        String query = "UPDATE USERS SET address = ?, phone = ?, password = ? WHERE name = ? AND password = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, updatedAddress);
            statement.setString(2, updatedPhone);
            statement.setString(3, updatedPassword);
            statement.setString(4, username);
            statement.setString(5, password);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Profile updated successfully.");
                this.password = updatedPassword; // Update the current password for future requests
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred while updating profile.");
        }
    }

    /**
     * Handles the "Cancel" button action to navigate back to the dashboard.
     */
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Dashboard.fxml"));
            Parent dashboardView = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setLoginCredentials(username, password);

            Stage stage = (Stage) usernameLabel.getScene().getWindow(); // Use usernameLabel
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to show an alert dialog.
     */
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
