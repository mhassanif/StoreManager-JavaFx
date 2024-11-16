package com.storemanager;

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
}
