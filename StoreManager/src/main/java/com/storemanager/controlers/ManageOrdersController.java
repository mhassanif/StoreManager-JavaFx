package com.storemanager.controlers;

import com.storemanager.dao.OrderDAO;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.users.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.sql.SQLException;
import java.util.List;

public class ManageOrdersController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, String> customerUsernameColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, String> orderStatusColumn;
    @FXML private TableColumn<Order, Double> totalPriceColumn;

    @FXML
    private void initialize() {
        // Set up the cell value factories for each column
        orderIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getOrderId())) // Handle int as String
        );
        customerUsernameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCustomer().getUsername())
        );
        orderDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOrderDate())
        );
        orderStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus())
        );
        totalPriceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject() // Bind the Double property
        );

        // Load data into the table
        loadOrders();
    }

    // Load orders into the TableView
    private void loadOrders() {
        try {
            List<Order> orders = OrderDAO.getAllOrders(); // Fetch orders from the database using OrderDAO
            ordersTable.getItems().setAll(orders);  // Populate the TableView
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately (you may show an error message to the user)
        }
    }

    @FXML
    public void handleDeleteOrder(ActionEvent event) {
        Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            System.out.println("Deleting Order: " + selectedOrder.getOrderId());
            try {
                // Remove from the database
                boolean success = OrderDAO.deleteOrder(selectedOrder.getOrderId());
                if (success) {
                    ordersTable.getItems().remove(selectedOrder); // Remove from table
                    System.out.println("Order deleted successfully.");
                } else {
                    System.out.println("Failed to delete order.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle SQL exception, maybe show an error message
            }
        } else {
            System.out.println("No order selected for deletion.");
        }
    }
}
