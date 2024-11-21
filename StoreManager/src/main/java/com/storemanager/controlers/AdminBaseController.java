package com.storemanager.controlers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;

public class AdminBaseController {

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnManageUsers;
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

    // Method to load and replace the entire window with new content
    protected void loadNewWindow(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/" + fxmlFile));
            Parent newRoot = loader.load();


            // Get the current stage
            Stage currentStage = (Stage) btnDashboard.getScene().getWindow();

            // Set new content on the stage
            Scene newScene = new Scene(newRoot);
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDashboard() {
        if (!"AdminDashboard".equals(currentView)) {
            loadNewWindow("AdminDashboard.fxml");
            currentView = "AdminDashboard";
        }
    }

    @FXML
    public void handleManageUsers() {
        if (!"ManageUsers".equals(currentView)) {
            loadNewWindow("ManageUsers.fxml");
            currentView = "ManageUsers";
        }
    }

    @FXML
    public void handleManageOrders() {
        if (!"ManageOrders".equals(currentView)) {
            loadNewWindow("ManageOrders.fxml");
            currentView = "ManageOrders";
        }
    }

    @FXML
    public void handleManageProducts() {
        if (!"ManageProducts".equals(currentView)) {
            loadNewWindow("ManageProducts.fxml");
            currentView = "ManageProducts";
        }
    }

    @FXML
    public void handleManageNotifications() {
        if (!"ManageNotifications".equals(currentView)) {
            loadNewWindow("ManageNotifications.fxml");
            currentView = "ManageNotifications";
        }
    }

    @FXML
    public void handleManageFeedback() {
        if (!"StaffManageFeedback".equals(currentView)) {
            loadNewWindow("StaffManageFeedback.fxml");
            currentView = "StaffManageFeedback";
        }
    }

    @FXML
    public void handleLogout() {
        System.out.println("Logging out...");
        loadNewWindow("Login.fxml"); // Example: Redirect to login screen
        currentView = "";
    }
}
