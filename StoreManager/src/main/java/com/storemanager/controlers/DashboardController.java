package com.storemanager.controlers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label dummyLabel;


    /**
     * Handle navigation to Categories.
     */
    @FXML
    public void handleCategories(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Categories.fxml"));
            Parent root = loader.load();

            // Get the Stage dynamically from the event
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Categories");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Handle navigation to Products.
     */
    @FXML
    private void handleProducts() {
        System.out.println("Navigating to Products...");
        // Load Products view here
    }

    /**
     * Handle navigation to Cart.
     */


    /**
     * Handle navigation to Orders.
     */
    @FXML
    private void handleOrders() {
        System.out.println("Navigating to Orders...");
        // Load Orders view here
    }

    /**
     * Handle navigation to Profile.
     */
    @FXML
    private void handleProfile() {
        System.out.println("Navigating to Profile...");
        // Load Profile view here
    }

    /**
     * Handle Logout.
     */
    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        // Add logic to return to Login page
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("You have been logged out.");
        alert.showAndWait();
        // Load Login view
    }
    @FXML
    public void handleCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Cart.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) dummyLabel.getScene().getWindow();

            Scene cartScene = new Scene(root);
            stage.setScene(cartScene);
            stage.setTitle("Shopping Cart");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
