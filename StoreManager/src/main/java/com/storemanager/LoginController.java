/*package com.storemanager;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Replace this with database authentication logic
        if ("user".equals(username) && "pass".equals(password)) {
            messageLabel.setText("Login successful!");
            System.out.println("Login successful!");
        } else {
            messageLabel.setText("Invalid credentials");
            System.out.println("Invalid credentials");
        }


    }
    public void hlDbOnAction() {
        // Example logic for database setup
        System.out.println("Database Setup clicked.");
        messageLabel.setText("Redirecting to Database Setup...");
        // Add your logic to navigate to a Database Setup page or dialog
    }
    public void hlCreateAnAccount() {
        // Example logic for account creation
        System.out.println("Create an Account clicked.");
        messageLabel.setText("Redirecting to Account Creation...");
        // Add your logic to navigate to an account creation form
    }
}*/
package com.storemanager;

import com.storemanager.db.DBconnector;
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
import javafx.application.Application;

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
            loadDashboard();
            return resultSet.next(); // If a row exists, authentication succeeded
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error connecting to the database.");
        }
        return false;
    }

    /**
     * Handle the "Database Setup" hyperlink click event.
     */
    public void hlDbOnAction() {
        System.out.println("Database Setup clicked.");
        messageLabel.setText("Redirecting to Database Setup...");
        // Implement your database setup navigation or logic here
    }

    /**
     * Handle the "Create an Account" hyperlink click event.
     */
    public void hlCreateAnAccount() {
        System.out.println("Create an Account clicked.");
        messageLabel.setText("Redirecting to Account Creation...");
        // Implement your account creation navigation or logic here
    }
    private void loadDashboard() {
        try {
            // Load the Dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load dashboard.");
        }
    }

}

