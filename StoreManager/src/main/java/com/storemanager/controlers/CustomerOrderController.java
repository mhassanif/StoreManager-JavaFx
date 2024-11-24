package com.storemanager.controlers;

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

    private String username;
    private String password;

    public void setLoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        System.out.println("setLoginCredentials called in CustomerOrderController with username: " + username);
        loadOrderHistory();
    }

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    /*
    private void loadOrderHistory() {
        System.out.println("Executing loadOrderHistory...");
        int userId = getCustomerIdFromDatabase();
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

    private int getCustomerIdFromDatabase() {
        String query = "SELECT user_id FROM USERS WHERE name = ? AND password = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            System.out.println("Fetching user ID for username: " + username + " and password: " + password);

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                System.out.println("User ID retrieved: " + userId);
                return userId;
            } else {
                System.err.println("No user found for the given credentials.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching user ID: " + e.getMessage());
        }
        return -1; // Return -1 if the user ID could not be fetched
    }

    private List<Order> fetchOrdersFromDatabase(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM ORDERTABLE WHERE customer_id = ?"; // Treating customer_id as user_id

        System.out.println("Fetching orders for user ID: " + userId);

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Debug: Print each field from the ResultSet
                System.out.println("Order ID: " + resultSet.getInt("order_id"));
                System.out.println("Order Date: " + resultSet.getString("order_date"));
                System.out.println("Total Amount: " + resultSet.getDouble("total_amount"));
                System.out.println("Status: " + resultSet.getString("status"));

                // Create Order object using a parameterized constructor
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
    */
    private void loadOrderHistory() {
        System.out.println("Executing loadOrderHistory...");
        int userId = getUserIdFromDatabase();
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

    private int getUserIdFromDatabase() {
        String query = "SELECT user_id FROM USERS WHERE name = ? AND password = ?";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            System.out.println("Fetching user ID for username: " + username + " and password: " + password);

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                System.out.println("User ID retrieved: " + userId);
                return userId;
            } else {
                System.err.println("No user found for the given credentials.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching user ID: " + e.getMessage());
        }
        return -1; // Return -1 if the user ID could not be fetched
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
                        null, // Customer object (can be null for now)
                        new ArrayList<>(), // Order items (can be populated later if needed)
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
