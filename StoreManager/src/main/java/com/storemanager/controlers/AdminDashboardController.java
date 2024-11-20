package com.storemanager.controlers;
import com.storemanager.controlers.AdminBaseController;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class AdminDashboardController extends AdminBaseController {

    @FXML
    private VBox contentArea;

    @FXML
    private Button btnDashboard, btnManageUsers, btnManageOrders, btnManageProducts, btnManageNotifications, btnManageFeedback, btnLogout;

    @FXML
    public void handleLogout() {
        // Add logout logic here
        System.out.println("Logging out...");
        // Redirect to login screen or close the app
    }

    /**
     * Helper method to load the relevant content into the content area.
     *
     * @param fxmlFile The name of the FXML file to load.
     * @throws IOException If the FXML file cannot be loaded.
     */
/*    private void loadContent(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/view/" + fxmlFile));
        Node content = loader.load();
        contentArea.getChildren().setAll(content);
    }*/
}
