package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.db.DBconnector;
import com.storemanager.model.order.Order;
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
        System.out.println("Executing loadOrderHistory...");
        int userId = CurrentUser.getInstance().getUser().getId(); // Fetch logged-in user ID
        System.out.println("Retrieved user ID: " + userId);

        if (userId != -1) {
            List<Order> orders = fetchOrdersFromDatabase(userId);
            System.out.println("Orders to display: " + orders.size());
            orders.forEach(order -> System.out.println(order)); // Debug: Print each order

            orderTable.setItems(FXCollections.observableArrayList(orders));
        } else {
            System.err.println("Invalid user ID. Cannot load orders.");
        }
    }

    private List<Order> fetchOrdersFromDatabase(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM ORDERTABLE WHERE customer_id = ?";

        System.out.println("Fetching orders for user ID: " + userId);

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Order ID: " + resultSet.getInt("order_id"));
                System.out.println("Order Date: " + resultSet.getString("order_date"));
                System.out.println("Total Amount: " + resultSet.getDouble("total_amount"));
                System.out.println("Status: " + resultSet.getString("status"));

                Order order = new Order(
                        null, // Customer object (optional)
                        new ArrayList<>(), // Order items (optional)
                        resultSet.getInt("order_id"),
                        resultSet.getString("order_date"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getString("status")
                );

                orders.add(order);
            }
            System.out.println("Total orders fetched: " + orders.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching orders: " + e.getMessage());
        }

        return orders;
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

