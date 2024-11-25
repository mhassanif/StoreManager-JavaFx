package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.model.users.User;
import com.storemanager.model.users.WarehouseStaff;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class WarehouseProfileController {

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

    private User user;

    @FXML
    public void initialize(){
        setWarehouseStaff();
    }

    public void setWarehouseStaff() {
        this.user= CurrentUser.getInstance().getUser();

        if (user != null) {
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            addressField.setText(user.getAddress());
            phoneField.setText(user.getPhoneNumber());
        } else {
            System.err.println("User object is null in WarehouseProfileController.");
        }
    }

    @FXML
    private void handleUpdateProfile() {
        if (user != null) {
            user.setAddress(addressField.getText());
            user.setPhoneNumber(phoneField.getText());
            System.out.println("Profile updated for: " + user.getUsername());
        } else {
            System.err.println("Cannot update profile. WarehouseStaff object is null.");
        }
    }

    @FXML
    private void handleChangePassword() {
        if (user != null) {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!user.getPassword().equals(currentPassword)) {
                System.err.println("Current password is incorrect.");
                return;
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                System.err.println("New password fields cannot be empty.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                System.err.println("New password and confirm password do not match.");
                return;
            }

            user.setPassword(newPassword);
            System.out.println("Password updated successfully for: " + user.getUsername());
        } else {
            System.err.println("Cannot change password. WarehouseStaff object is null.");
        }
    }
}
