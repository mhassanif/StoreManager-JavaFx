package com.storemanager.controlers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

import java.io.IOException;

public class AdminDashboardController {

    public Button btnProfile;
    @FXML
    private VBox contentArea;

    @FXML
    private Button btnManageUsers, btnManageOrders, btnManageProducts, btnManageNotifications, btnManageFeedback, btnLogout;

    private String currentView = "";


    /**
     * Helper method to load the relevant content into the content area.
     *
     * @param fxmlFile The name of the FXML file to load.
     */
    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/" + fxmlFile));
            Node content = loader.load();
            contentArea.getChildren().setAll(content);
            currentView = fxmlFile; // Track the current view
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
    public void loadContentToDashboard(String fxmlFile) {
        loadContent(fxmlFile); // This calls the private loadContent method
    }


    @FXML
    public void handleProfile() {
        if (!"WarehouseProfile.fxml".equals(currentView)) {
            loadContent("WarehouseProfile.fxml");
            currentView = "WarehouseProfile";
        }
    }

    @FXML
    public void handleManageUsers() {
        if (!"ManageUsers.fxml".equals(currentView)) {
            loadContent("ManageUsers.fxml");
            currentView = "ManageUsers";
        }
    }

    @FXML
    public void handleManageOrders() {
        if (!"ManageOrders.fxml".equals(currentView)) {
            loadContent("ManageOrders.fxml");
            currentView = "ManageOrders";
        }
    }

    @FXML
    public void handleManageProducts() {
        if (!"ManageProducts.fxml".equals(currentView)) {
            loadContent("ManageProducts.fxml");
            currentView = "ManageProducts";
        }
    }

    @FXML
    public void handleManageNotifications() {
        if (!"ManageNotifications.fxml".equals(currentView)) {
            loadContent("ManageNotifications.fxml");
            currentView = "ManageNotifications";
        }
    }

    @FXML
    public void handleManageFeedback() {
        if (!"StaffManageFeedback.fxml".equals(currentView)) {
            loadContent("StaffManageFeedback.fxml");
            currentView = "StaffManageFeedback";
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
