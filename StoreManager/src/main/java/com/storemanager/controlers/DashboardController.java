package com.storemanager.controlers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    private String username;
    private String password;

    public void setLoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        System.out.println("Credentials set in DashboardController: " + username);
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
        loadContent("/com/storemanager/CustomerOrder.fxml");
    }

    @FXML
    private void showNotifications() {
        loadContent("/com/storemanager/CustomerNotification.fxml");
    }

    @FXML
    private void showProfile() {
        loadContentWithCredentials("/com/storemanager/CustomerProfile.fxml");
    }

    @FXML
    private void showFeedback() {
        loadContentWithCredentials("/com/storemanager/CustomerFeedback.fxml");
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

    private void loadContentWithCredentials(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();

            // Pass credentials to the corresponding controller
            if (fxmlFile.equals("/com/storemanager/CustomerOrder.fxml")) {
                CustomerOrderController customerOrderController = loader.getController();
                customerOrderController.setLoginCredentials(username, password);
            } else if (fxmlFile.equals("/com/storemanager/CustomerProfile.fxml")) {
                CustomerProfileController profileController = loader.getController();
                profileController.setLoginCredentials(username, password);
            } else if (fxmlFile.equals("/com/storemanager/CustomerFeedback.fxml")) {
                CustomerFeedbackController feedbackController = loader.getController();
                feedbackController.setLoginCredentials(username, password);
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
                    // Navigate to login page
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
