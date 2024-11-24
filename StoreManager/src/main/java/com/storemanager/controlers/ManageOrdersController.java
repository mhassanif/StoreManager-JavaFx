package com.storemanager.controlers;

import com.storemanager.dao.OrderDAO;
import com.storemanager.model.order.Order;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageOrdersController {

    @FXML
    private VBox contentArea;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, Integer> customerIdColumn;

    @FXML
    private TableColumn<Order, String> customerNameColumn;

    @FXML
    private TableColumn<Order, Integer> paymentIdColumn;

    @FXML
    private TableColumn<Order, String> orderDateColumn;

    @FXML
    private TableColumn<Order, Double> totalAmountColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private Button btnDeleteOrder, btnUpdateOrderStatus;

    private List<Order> allOrders;

    @FXML
    public void initialize() {
        // Initialize table columns
        orderIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        customerIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomer().getCustomerId()).asObject());
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getUsername()));
        paymentIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPayment().getId()).asObject());
        orderDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate()));
        totalAmountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Load orders from database
        loadOrders();

        // Populate status filter dropdown
        statusFilter.getItems().addAll("Pending", "Completed", "Cancelled");
    }

    private void loadOrders() {
        try {
            // Fetch all orders along with payment details from the database
            allOrders = OrderDAO.getAllOrders();
            ordersTable.getItems().setAll(allOrders);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load orders.", e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Order> filteredOrders = allOrders.stream()
                .filter(order -> order.getCustomer().getUsername().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        ordersTable.getItems().setAll(filteredOrders);
    }

    @FXML
    public void handleFilterStatus() {
        String selectedStatus = statusFilter.getValue();
        if (selectedStatus != null) {
            List<Order> filteredByStatus = allOrders.stream()
                    .filter(order -> order.getStatus().equals(selectedStatus))
                    .collect(Collectors.toList());
            ordersTable.getItems().setAll(filteredByStatus);
        } else {
            ordersTable.getItems().setAll(allOrders);
        }
    }

    @FXML
    public void handleDeleteOrder() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            try {
                // Delete the order from the database using the DAO
                boolean success=OrderDAO.deleteOrder(selectedOrder.getOrderId());
                allOrders.remove(selectedOrder);
                ordersTable.getItems().setAll(allOrders);
                System.out.println("Deleted Order: " + selectedOrder.getOrderId());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete order.", e.getMessage());
            }
        } else {
            System.out.println("No order selected for deletion");
        }
    }

    @FXML
    public void handleUpdateOrderStatus() {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            // Create a list of possible statuses for the dropdown
            List<String> statuses = new ArrayList<>();
            statuses.add("Completed");
            statuses.add("Pending");
            statuses.add("Cancelled");

            // Open dialog to ask for new order status with a dropdown
            ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedOrder.getStatus(), statuses);
            dialog.setTitle("Update Order Status");
            dialog.setHeaderText("Select the new status for Order ID: " + selectedOrder.getOrderId());
            dialog.setContentText("New Status:");

            // Wait for the user's input
            dialog.showAndWait().ifPresent(selectedStatus -> {
                try {
                    // Update the order status in the database using the DAO
                    OrderDAO.updateOrderStatus(selectedOrder.getOrderId(), selectedStatus);

                    // Update the order object and refresh the table
                    selectedOrder.setStatus(selectedStatus);
                    ordersTable.refresh();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update order status.", e.getMessage());
                }
            });
        } else {
            System.out.println("No order selected to update status");
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
