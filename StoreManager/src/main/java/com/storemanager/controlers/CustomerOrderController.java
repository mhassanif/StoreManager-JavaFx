package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.OrderDAO;
import com.storemanager.model.order.Order;
import com.storemanager.model.users.Customer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerOrderController {

    @FXML
    private VBox contentArea;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> orderDateColumn;

    @FXML
    private TableColumn<Order, Double> totalPriceColumn;

    @FXML
    private TableColumn<Order, String> orderStatusColumn;

    @FXML
    private TableColumn<Order, String> paymentStatusColumn;

    @FXML
    private TableColumn<Order, String> paymentDateColumn;

    private List<Order> allOrders;

    @FXML
    public void initialize() {
        // Initialize table columns
        orderIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        orderDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate()));
        totalPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());
        orderStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        paymentStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPayment().getStatus()));
        paymentDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPayment().getDate()));

        // Load order history for the logged-in customer
        loadOrderHistory();

        // Populate status filter dropdown
        statusFilterComboBox.getItems().addAll("All", "Pending", "Completed", "Cancelled");
        statusFilterComboBox.setValue("All");

        // Add event listener for filtering
        statusFilterComboBox.setOnAction(e -> filterOrdersByStatus());
    }

    public void loadOrderHistory() {
        int customerId = ((Customer) CurrentUser.getInstance().getUser()).getCustomerId();
        try {
            allOrders = OrderDAO.getOrdersByCustomerId(customerId);
            orderTable.getItems().setAll(allOrders);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load orders.", e.getMessage());
        }
    }

    @FXML
    private void filterOrdersByStatus() {
        String selectedStatus = statusFilterComboBox.getValue();
        if ("All".equals(selectedStatus)) {
            orderTable.getItems().setAll(allOrders);
        } else {
            List<Order> filteredOrders = allOrders.stream()
                    .filter(order -> order.getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
            orderTable.getItems().setAll(filteredOrders);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
