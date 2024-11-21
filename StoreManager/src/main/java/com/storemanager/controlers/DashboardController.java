package com.storemanager.controlers;

import com.storemanager.model.cart.ShoppingCart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

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
        loadContent("/com/storemanager/CustomerProfile.fxml");
    }

    private static ShoppingCart activeCart = new ShoppingCart(1); // Shared cart (replace with DB ID if needed)

    public static ShoppingCart getActiveCart() {
        return activeCart;
    }
    /**
     * Helper method to load and display content in the `contentArea`.
     *
     * @param fxmlFile The FXML file to load (relative path).
     */
    private void loadContent(String fxmlFile) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node content = loader.load();

            // Clear existing content and add the new one
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML file: " + fxmlFile);
        }
    }
}
