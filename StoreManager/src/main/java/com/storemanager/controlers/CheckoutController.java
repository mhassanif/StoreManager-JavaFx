package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.OrderDAO;
import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.ShoppingCartDAO;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {

    @FXML private Label totalAmountLabel;
    @FXML private Button confirmButton;
    @FXML private TableView<CartItem> orderSummaryTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colTotal;

    private ShoppingCart cart;
    private double totalAmount;
    private Customer customer;

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

        // Get the current customer
        customer = (Customer) CurrentUser.getInstance().getUser();
    }

    public void setCartDetails(ShoppingCart cart) {
        this.cart = cart;
        ObservableList<CartItem> cartItems = FXCollections.observableArrayList(cart.getItems());
        orderSummaryTable.setItems(cartItems);
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        totalAmountLabel.setText("Total Amount: $" + String.format("%.2f", totalAmount));
    }

    @FXML
    private void handleConfirmPayment() {
        if (customer == null) {
            showErrorDialog("Error", "User not logged in.");
            return;
        }

        double customerBalance = customer.getBalance();
        if (customerBalance >= totalAmount) {
            // Deduct the amount from customer's balance
            boolean balanceUpdated = deductWalletBalance(customer.getCustomerId(), totalAmount);

            if (balanceUpdated) {
                ShoppingCartDAO.clearCart(((Customer)CurrentUser.getInstance().getUser()).getShoppingCart().getCartId());
                saveOrder(customer.getCustomerId());
                showSuccessDialog();
                navigateToDashboard();
            } else {
                showErrorDialog("Payment Failed", "Unable to process payment. Please try again.");
            }
        } else {
            showInsufficientBalanceDialog();
        }
    }

    private boolean deductWalletBalance(int customerId, double amount) {
        double newBalance = customer.getBalance() - amount;

        // Update the balance in the database
        boolean success = CustomerDAO.setCustomerBalance(customerId, newBalance);

        if (success) {
            // Update the balance in the customer object
            customer.setBalance(newBalance);
            return true;
        } else {
            return false;
        }
    }

    private void saveOrder(int customerId) {
        try {
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem item : cart.getItems()) {
                OrderItem orderItem = new OrderItem(item.getProduct(), item.getQuantity(), item.getProduct().getPrice());
                orderItems.add(orderItem);
            }

            Order order = new Order(customer, orderItems);
            order.setTotalPrice(totalAmount);
            order.setStatus("Pending");

            OrderDAO.createOrder(order);
            // Do not clear the cart as per the requirement
            // cart.clearCart();

            savePayment(order.getOrderId(), totalAmount);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Order Saving Failed", "An error occurred while saving the order. Please try again.");
        }
    }

    private void savePayment(int orderId, double amount) {
        String paymentQuery = "INSERT INTO PAYMENT (order_id, amount, date, status) VALUES (?, ?, GETDATE(), 'Pending')";
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

    private void showInsufficientBalanceDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Insufficient Balance");
        alert.setHeaderText(null);
        alert.setContentText("Your balance is insufficient to complete this purchase. Please recharge your account and try again.");
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
    private void confirmOrder() {
        handleConfirmPayment();
    }
}
