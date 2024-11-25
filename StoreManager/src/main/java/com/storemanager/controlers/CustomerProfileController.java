package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.model.users.User;
import com.storemanager.db.DBconnector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    /**
     * Initializes the profile controller and loads the user's profile.
     */
    @FXML
    public void initialize() {
        loadUserProfile();
    }

    /**
     * Loads the user's profile details from the database using CurrentUser.
     */
    private void loadUserProfile() {
        User currentUser = CurrentUser.getInstance().getUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is currently logged in.");
            return;
        }

        String query = "SELECT * FROM USERS WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, currentUser.getId());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                usernameLabel.setText(resultSet.getString("name"));
                emailLabel.setText(resultSet.getString("email"));
                addressField.setText(resultSet.getString("address"));
                phoneField.setText(resultSet.getString("phone"));
                passwordField.setText(resultSet.getString("password")); // Prepopulate the password
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to load user profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the profile.");
        }
    }

    /**
     * Handles the "Save Changes" button action to update the user's profile.
     */
    @FXML
    private void handleSaveChanges() {
        User currentUser = CurrentUser.getInstance().getUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is currently logged in.");
            return;
        }

        String updatedAddress = addressField.getText();
        String updatedPhone = phoneField.getText();
        String updatedPassword = passwordField.getText();

        String query = "UPDATE USERS SET address = ?, phone = ?, password = ? WHERE user_id = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, updatedAddress);
            statement.setString(2, updatedPhone);
            statement.setString(3, updatedPassword);
            statement.setInt(4, currentUser.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating profile.");
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

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate back to Dashboard.");
        }
    }

    /**
     * Helper method to show an alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
