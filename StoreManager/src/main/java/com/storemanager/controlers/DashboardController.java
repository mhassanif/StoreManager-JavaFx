package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Ensure that a user is logged in
        if (CurrentUser.getInstance().getUser() == null) {
            throw new IllegalStateException("No user is currently logged in.");
        }

        System.out.println("Dashboard initialized for user: " + CurrentUser.getInstance().getUser().getUsername());
    }

    @FXML
    private void showProducts() {
        loadContent("/com/storemanager/CustomerProducts.fxml");
    }

    @FXML
    private void showCart() {
        loadContent("/com/storemanager/Cart.fxml");
    }

    @FXML
    private void showOrders() {
        // Directly load the CustomerOrder.fxml and handle orders using CurrentUser
        loadContentWithController("/com/storemanager/CustomerOrder.fxml", "CustomerOrderController");
    }

    @FXML
    private void showNotifications() {
        loadContent("/com/storemanager/CustomerNotification.fxml");
    }

    @FXML
    private void showProfile() {
        // Load profile without passing credentials
        loadContent("/com/storemanager/CustomerProfile.fxml");
    }

    @FXML
    private void showFeedback() {
        // Load feedback without passing credentials
        loadContent("/com/storemanager/CustomerFeedback.fxml");
    }

    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML file: " + fxmlFile);
        }
    }

    private void loadContentWithController(String fxmlFile, String controllerType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();

            // Handle specific controllers, like CustomerOrderController
            if ("CustomerOrderController".equals(controllerType)) {
                CustomerOrderController customerOrderController = loader.getController();
                customerOrderController.loadOrderHistory(); // Uses CurrentUser
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML file: " + fxmlFile);
        }
    }

    @FXML
    private void handleSignOut() {
        // Show confirmation dialog
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Sign Out");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to sign out?");
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equalsIgnoreCase("OK")) {
                try {
                    // Clear CurrentUser and navigate to login page
                    CurrentUser.getInstance().clearUser();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Login.fxml"));
                    Parent loginView = loader.load();

                    Stage stage = (Stage) contentArea.getScene().getWindow();
                    stage.setScene(new Scene(loginView));
                    stage.setTitle("Login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
