package com.storemanager.controlers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;

public class ManagerDashboardController {

    @FXML
    private VBox sidebar;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnManageOrders;
    @FXML
    private Button btnManageProducts;
    @FXML
    private Button btnManageNotifications;
    @FXML
    private Button btnManageFeedback;
    @FXML
    private Button btnLogout;

    private String currentView = "";

    // Helper method to load new scenes and replace the current window
    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/view/" + fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) sidebar.getScene().getWindow(); // Get current stage
            stage.setScene(scene); // Set the new scene
            stage.show(); // Show the updated stage
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDashboard() {
        if (!"ManagerDashboard".equals(currentView)) {
            loadScene("ManagerDashboard.fxml");
            currentView = "ManagerDashboard";
        }
    }

    @FXML
    public void handleManageOrders() {
        if (!"ManageOrders".equals(currentView)) {
            loadScene("ManageOrders.fxml");
            currentView = "ManageOrders";
        }
    }

    @FXML
    public void handleManageProducts() {
        if (!"ManageProducts".equals(currentView)) {
            loadScene("ManageProducts.fxml");
            currentView = "ManageProducts";
        }
    }

    @FXML
    public void handleManageNotifications() {
        if (!"ManageNotifications".equals(currentView)) {
            loadScene("ManageNotifications.fxml");
            currentView = "ManageNotifications";
        }
    }

    @FXML
    public void handleManageFeedback() {
        if (!"ManageFeedback".equals(currentView)) {
            loadScene("ManageFeedback.fxml");
            currentView = "ManageFeedback";
        }
    }

    @FXML
    public void handleLogout() {
        System.out.println("Logging out...");
        loadScene("Login.fxml"); // Redirect to the login screen
        currentView = ""; // Reset the current view
    }
}
