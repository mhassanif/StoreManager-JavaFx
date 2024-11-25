package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.model.users.WarehouseStaff;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class WarehouseDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Ensure the logged-in user is a WarehouseStaff
        if (!(CurrentUser.getInstance().getUser() instanceof WarehouseStaff)) {
            throw new IllegalStateException("Logged-in user is not a Warehouse Staff.");
        }

        System.out.println("WarehouseDashboard initialized for user: " + CurrentUser.getInstance().getUser().getUsername());
    }

    @FXML
    private void showInventory() {
        loadContent("/com/storemanager/WarehouseInventory.fxml");
    }

    @FXML
    private void showNotifications() {
        loadContent("/com/storemanager/WarehouseNotifications.fxml");
    }

    @FXML
    private void showProfile() {
        loadContent("/com/storemanager/WarehouseProfile.fxml");
    }

    @FXML
    private void handleSignOut() {
        // Clear the current user and navigate to the login page
        CurrentUser.getInstance().clearUser();

        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Login.fxml"));
            Parent loginView = loader.load();
            stage.setScene(new Scene(loginView));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
