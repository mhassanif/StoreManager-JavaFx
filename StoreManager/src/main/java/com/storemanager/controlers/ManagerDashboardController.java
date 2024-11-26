package com.storemanager.controlers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

public class ManagerDashboardController {

    public Button btnProfile;
    @FXML
    private VBox sidebar;

    @FXML
    private VBox contentArea; // The main content area (right side) to load the FXMLs

    @FXML
    private Button btnManageOrders;
    @FXML
    private Button btnManageProducts;
    @FXML
    private Button btnManageNotifications;
    @FXML
    private Button btnLogout;

    private String currentView = "";

    // Helper method to load FXML into content area (right side of the BorderPane)
    private void loadContent(String fxmlFile) {
        try {
            // Load the FXML into a Node (VBox)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/" + fxmlFile));
            Node content = loader.load();

            // Clear the existing content and add the new content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleProfile() {
        if (!"WarehouseProfile.fxml".equals(currentView)) {
            loadContent("WarehouseProfile.fxml");
            currentView = "WarehouseProfile";
        }
    }

    @FXML
    public void handleManageOrders() {
        if (!"ManageOrders".equals(currentView)) {
            loadContent("ManageOrders.fxml");
            currentView = "ManageOrders";
        }
    }

    @FXML
    public void handleManageProducts() {
        if (!"ManageProducts".equals(currentView)) {
            loadContent("ManageProducts.fxml");
            currentView = "ManageProducts";
        }
    }

    @FXML
    public void handleManageNotifications() {
        if (!"ManageNotifications".equals(currentView)) {
            loadContent("ManageNotifications.fxml");
            currentView = "ManageNotifications";
        }
    }

    @FXML
    public void handleLogout() {
        try {
            // Redirect to login screen by loading the Login FXML as the new root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Login.fxml"));
            Parent loginRoot = loader.load();  // Parent is used here instead of Node

            // Replace the entire scene's root with the Login view
            contentArea.getScene().setRoot(loginRoot);
        } catch (IOException e) {
            System.err.println("Failed to load Login.fxml");
            e.printStackTrace();
        }
    }
}
