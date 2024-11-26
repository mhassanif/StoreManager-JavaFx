package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.OrderDAO;
import com.storemanager.db.DBconnector;
import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.users.Customer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {

    @FXML private Label totalAmountLabel;
    @FXML private TextArea deliveryAddressField;
    @FXML private Button confirmButton;
    @FXML private TableView<CartItem> orderSummaryTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colTotal;

    private ShoppingCart cart;
    private double totalAmount;

    // Initialize the table columns
    @FXML
    public void initialize() {
        colProductName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduct().getName()));
        colQuantity.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        colTotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().calculateSubtotal()).asObject());
    }

    public void setCartDetails(ShoppingCart cart) {
        this.cart = cart;
        ObservableList<CartItem> cartItems = FXCollections.observableArrayList(cart.getItems());
        orderSummaryTable.setItems(cartItems);
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        totalAmountLabel.setText("$" + String.format("%.2f", totalAmount));
    }

    @FXML
    private void handleConfirmPayment() {
        int userId = CurrentUser.getInstance().getUser().getId();

        if (deductWalletBalance(userId, totalAmount)) {
            saveOrder(userId);
            showSuccessDialog();
            navigateToDashboard();
        } else {
            showErrorDialog("Payment Failed", "Insufficient wallet balance. Please recharge and try again.");
        }
    }

    private boolean deductWalletBalance(int userId, double amount) {
        System.out.println("Deducting $" + amount + " from hardcoded wallet balance.");
        return true; // Simulate successful wallet deduction
    }

    private void saveOrder(int userId) {
        try {
            Customer customer = (Customer) CurrentUser.getInstance().getUser();
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem item : cart.getItems()) {
                OrderItem orderItem = new OrderItem(item.getProduct(), item.getQuantity(), item.getProduct().getPrice());
                orderItems.add(orderItem);
            }

            Order order = new Order(customer, orderItems);
            order.setTotalPrice(totalAmount);
            order.setStatus("Completed");

            OrderDAO.createOrder(order);
            cart.clearCart();
            savePayment(order.getOrderId(), totalAmount);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Order Saving Failed", "An error occurred while saving the order. Please try again.");
        }
    }

    private void savePayment(int orderId, double amount) {
        String paymentQuery = "INSERT INTO PAYMENT (order_id, amount, date, status) VALUES (?, ?, GETDATE(), 'Completed')";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(paymentQuery)) {
            ps.setInt(1, orderId);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("Your payment was successful!");
        alert.setContentText("Thank you for your purchase.");
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            // Load Dashboard.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Replace the current scene with the Dashboard
            Stage stage = (Stage) totalAmountLabel.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goBackToCart() {
        try {
            // Load Cart.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Cart.fxml"));
            Node cartContent = loader.load();

            // Access the DashboardController and set the content dynamically
            Stage stage = (Stage) totalAmountLabel.getScene().getWindow();
            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/com/storemanager/Dashboard.fxml"));
            Parent dashboardRoot = dashboardLoader.load();
            DashboardController dashboardController = dashboardLoader.getController();

            dashboardController.loadContent("/com/storemanager/Cart.fxml"); // Dynamically load Cart into the content area

            // Replace the current scene with the updated Dashboard
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Dashboard - Cart");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void confirmOrder()
    {
        handleConfirmPayment();
    }
}
