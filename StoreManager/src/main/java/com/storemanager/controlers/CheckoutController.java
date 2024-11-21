package com.storemanager.controlers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CheckoutController {

    @FXML private Label checkoutSummaryLabel;

    @FXML
    public void initialize() {
        // Populate the checkout summary (fetch from ShoppingCart/Order system)
        checkoutSummaryLabel.setText("Your order total is $...");
    }

    public void confirmOrder() {
        System.out.println("Order confirmed!");
        // Implement order creation logic
    }
}
