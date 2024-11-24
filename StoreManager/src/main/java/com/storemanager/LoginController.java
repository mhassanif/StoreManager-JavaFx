package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.StaffDAO;
import com.storemanager.dao.UserDAO;
import com.storemanager.model.users.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField; // Updated to match FXML

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    /**
     * Handle the login action.
     */
    @FXML
    public void login() {
        String username = usernameField.getText().trim(); // Use usernameField for email input
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username or Password cannot be empty.");
            return;
        }

        User user = UserDAO.authenticate(username, password);

        if (user == null) {
            messageLabel.setText("Invalid username or password.");
            return;
        }

        User userObject;
        String role = user.getRole();
        String position = "";

        switch (role.toLowerCase()) {
            case "customer":
                int customerId = CustomerDAO.getCustomerByUserId(user.getId()).getCustomerId();
                userObject = new Customer(customerId, user.getId(), user.getUsername(), user.getEmail(),
                        user.getPassword(), user.getAddress(), user.getPhoneNumber());
                break;

            case "staff":
                position = StaffDAO.getStaffPositionByUserId(user.getId());
                int staffId = StaffDAO.getStaffIdByUserId(user.getId());
                switch (position.toLowerCase()) {
                    case "admin":
                        userObject = new Admin(staffId, user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                                user.getAddress(), user.getPhoneNumber());
                        break;

                    case "manager":
                        userObject = new Manager(staffId, user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                                user.getAddress(), user.getPhoneNumber());
                        break;

                    case "warehouse staff":
                        userObject = new WarehouseStaff(staffId, user.getId(), user.getUsername(), user.getEmail(),
                                user.getPassword(), user.getAddress(), user.getPhoneNumber());
                        break;

                    default:
                        messageLabel.setText("Unknown staff position: " + position);
                        return;
                }
                break;

            default:
                messageLabel.setText("Unknown user role: " + role);
                return;
        }

        // Set the logged-in user in the CurrentUser singleton
        CurrentUser.getInstance().setUser(userObject);

        loadDashboard(role, position);
    }

    /**
     * Load the appropriate dashboard based on the user role.
     */
    private void loadDashboard(String role, String position) {
        try {
            FXMLLoader loader;
            Parent root;

            // Load the correct dashboard based on the role
            if ("Customer".equalsIgnoreCase(role)) {
                loader = new FXMLLoader(getClass().getResource("/com/storemanager/Dashboard.fxml"));
            } else {
                switch (position.toLowerCase()) {
                    case "admin":
                        loader = new FXMLLoader(getClass().getResource("/com/storemanager/AdminDashboard.fxml"));
                        break;
                    case "manager":
                        loader = new FXMLLoader(getClass().getResource("/com/storemanager/ManagerDashboard.fxml"));
                        break;
                    case "warehouse staff":
                        loader = new FXMLLoader(getClass().getResource("/com/storemanager/WarehouseStaffDashboard.fxml"));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown role: " + role);
                }
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
        messageLabel.setText("Database Setup functionality not yet implemented.");
    }
}
