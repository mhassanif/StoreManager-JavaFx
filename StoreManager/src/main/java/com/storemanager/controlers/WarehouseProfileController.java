package com.storemanager.controlers;

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

    private WarehouseStaff warehouseStaff;

    public void setWarehouseStaff(WarehouseStaff warehouseStaff) {
        this.warehouseStaff = warehouseStaff;

        if (warehouseStaff != null) {
            usernameField.setText(warehouseStaff.getUsername());
            emailField.setText(warehouseStaff.getEmail());
            addressField.setText(warehouseStaff.getAddress());
            phoneField.setText(warehouseStaff.getPhoneNumber());
        } else {
            System.err.println("WarehouseStaff object is null in WarehouseProfileController.");
        }
    }

    @FXML
    private void handleUpdateProfile() {
        if (warehouseStaff != null) {
            warehouseStaff.setAddress(addressField.getText());
            warehouseStaff.setPhoneNumber(phoneField.getText());
            System.out.println("Profile updated for: " + warehouseStaff.getUsername());
        } else {
            System.err.println("Cannot update profile. WarehouseStaff object is null.");
        }
    }

    @FXML
    private void handleChangePassword() {
        if (warehouseStaff != null) {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (!warehouseStaff.getPassword().equals(currentPassword)) {
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

            warehouseStaff.setPassword(newPassword);
            System.out.println("Password updated successfully for: " + warehouseStaff.getUsername());
        } else {
            System.err.println("Cannot change password. WarehouseStaff object is null.");
        }
    }
}
