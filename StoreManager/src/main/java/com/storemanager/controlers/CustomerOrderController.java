package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.OrderDAO;
import com.storemanager.db.DBconnector;
import com.storemanager.model.order.Order;
import com.storemanager.model.users.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerOrderController {

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, String> orderDateColumn;

    @FXML
    private TableColumn<Order, Double> totalPriceColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Automatically load orders for the logged-in user
        loadOrderHistory();
    }

    /**
     * Fetches and displays the order history for the logged-in user.
     */
    public void loadOrderHistory() {
        int customerId = ((Customer) CurrentUser.getInstance().getUser()).getCustomerId();
        try {
            List<Order> orders = OrderDAO.getOrdersByCustomerId(customerId);

        } catch (SQLException e) {
            System.err.println("Database error while retrieving orders: " + e.getMessage());
        }
    }


    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Dashboard.fxml"));
            Parent dashboardView = loader.load();

            // Debug: Confirm navigation
            System.out.println("Navigating back to Dashboard...");

            Stage stage = (Stage) orderTable.getScene().getWindow();
            stage.setScene(new Scene(dashboardView));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to navigate back to Dashboard: " + e.getMessage());
        }
    }
}

