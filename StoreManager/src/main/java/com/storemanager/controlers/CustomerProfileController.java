package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.UserDAO;
import com.storemanager.model.users.Customer;
import com.storemanager.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller class for the Customer Profile view.
 * Allows customers to view and update their profile information.
 */
public class CustomerProfileController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label balanceLabel;

    private Customer customer;

    /**
     * Initializes the controller and loads the customer data.
     */
    @FXML
    public void initialize() {
        setCustomer();
    }

    /**
     * Sets the customer information in the profile fields.
     */
    public void setCustomer() {
        User user = CurrentUser.getInstance().getUser();

        if (user instanceof Customer) {
            this.customer = (Customer) user;
            usernameField.setText(customer.getUsername());
            emailField.setText(customer.getEmail());
            addressField.setText(customer.getAddress());
            phoneField.setText(customer.getPhoneNumber());
            updateBalanceLabel();
        } else {
            System.err.println("Current user is not a Customer in CustomerProfileController.");
        }
    }

    /**
     * Updates the balance label with the current balance.
     */
    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Current Balance: $%.2f", customer.getBalance()));
    }

    /**
     * Handles the action for updating the customer's profile information.
     */
    @FXML
    private void handleUpdateProfile() {
        if (customer != null) {
            customer.setAddress(addressField.getText());
            customer.setPhoneNumber(phoneField.getText());

            // Update the user in the database
            boolean success = UserDAO.updateUser(customer);
            if (success) {
                showAlert(AlertType.INFORMATION, "Success", "Profile updated successfully.");
                System.out.println("Profile updated for: " + customer.getUsername());
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update profile.");
            }
        } else {
            System.err.println("Cannot update profile. Customer object is null.");
        }
    }

    /**
     * Handles the action for changing the customer's password.
     */
    @FXML
    private void handleChangePassword() {
        if (customer != null) {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!customer.getPassword().equals(currentPassword)) {
                showAlert(AlertType.ERROR, "Error", "Current password is incorrect.");
                return;
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "New password fields cannot be empty.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showAlert(AlertType.ERROR, "Error", "New password and confirm password do not match.");
                return;
            }

            customer.setPassword(newPassword);

            // Update the user in the database
            boolean success = UserDAO.updateUser(customer);
            if (success) {
                showAlert(AlertType.INFORMATION, "Success", "Password updated successfully.");
                System.out.println("Password updated successfully for: " + customer.getUsername());
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update password.");
            }
        } else {
            System.err.println("Cannot change password. Customer object is null.");
        }
    }

    /**
     * Handles the action for recharging the customer's balance.
     */
    @FXML
    private void handleRecharge() {
        if (customer != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Recharge Balance");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter amount to recharge (max $5000 per transaction):");

            // Show the dialog and wait for user input
            dialog.showAndWait().ifPresent(input -> {
                try {
                    double amount = Double.parseDouble(input);

                    if (amount <= 0) {
                        showAlert(AlertType.ERROR, "Invalid Amount", "Amount must be greater than zero.");
                    } else if (amount > 5000) {
                        showAlert(AlertType.ERROR, "Invalid Amount", "Amount must not exceed $5000 per transaction.");
                    } else {
                        // Recharge the customer's balance
                        customer.recharge(amount);

                        // Update the balance label
                        updateBalanceLabel();

                        showAlert(AlertType.INFORMATION, "Success", String.format("Your balance has been increased by $%.2f.", amount));
                    }
                } catch (NumberFormatException e) {
                    showAlert(AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
                }
            });
        } else {
            System.err.println("Cannot recharge balance. Customer object is null.");
        }
    }

    /**
     * Helper method to display alerts.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
