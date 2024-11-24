package com.storemanager.controlers;

import com.storemanager.db.DBconnector;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerFeedbackController {

    @FXML
    private TextArea commentsField;

    private String username;
    private String password;
    private int customerId;

    public void setLoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        fetchCustomerId();
    }

    private void fetchCustomerId() {
        String query = "SELECT customer_id FROM CUSTOMER WHERE user_id = (SELECT user_id FROM USERS WHERE name = ? AND password = ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                customerId = resultSet.getInt("customer_id");
                System.out.println("Customer ID retrieved: " + customerId);
            } else {
                System.err.println("Customer not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitFeedback() {
        String comments = commentsField.getText();

        if (comments.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Comments cannot be empty.");
            return;
        }

        String query = "INSERT INTO FEEDBACK (customer_id, comments) VALUES (?, ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, customerId);
            statement.setString(2, comments);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Feedback submitted successfully!");
                commentsField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit feedback.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while submitting feedback.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
