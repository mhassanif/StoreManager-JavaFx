package com.storemanager.controlers;

import com.storemanager.model.items.Category;
import com.storemanager.model.items.Product;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.users.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

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
                new SimpleStringProperty(String.valueOf(cellData.getValue().getOrderId())) // Corrected to handle int as String
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
                new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject() // Properly bind the Double property
        );

        // Load data into the table (this should be done dynamically)
        loadOrders();
    }

    // Load orders into the TableView
    private void loadOrders() {
        // Assuming you have a list of orders
/*        List<Order> orders = orderService.getAllOrders(); // This should be your method to get orders
        ordersTable.getItems().setAll(orders); */
    }

    // Example method to fetch orders from a data source (e.g., a database)
    private List<Order> fetchOrdersFromDatabase() {     //testing
        // Creating mock data for Category
        Category category1 = new Category(1, "Electronics");
        Category category2 = new Category(2, "Furniture");

        // Creating mock data for Product, linking to Category
        Product product1 = new Product(1, "Product A", 20.0, "Brand A", "imageA.jpg", category1, "Description of Product A");
        Product product2 = new Product(2, "Product B", 50.0, "Brand B", "imageB.jpg", category2, "Description of Product B");
        Product product3 = new Product(3, "Product C", 30.0, "Brand C", "imageC.jpg", category1, "Description of Product C");

        // Creating mock data for OrderItem, linking to Product
        OrderItem orderItem1 = new OrderItem(product1, 2, 20.0); // Order 1 Item
        OrderItem orderItem2 = new OrderItem(product2, 1, 50.0); // Order 2 Item
        OrderItem orderItem3 = new OrderItem(product3, 3, 30.0); // Order 3 Item

        // Creating mock Customers
        Customer customer1 = new Customer(1, "John Doe", "john.doe@example.com", "123", "456", "789");
        Customer customer2 = new Customer(2, "Jane Smith", "jane.smith@example.com", "123", "456", "789");

        // Creating Orders with Customer and OrderItem
        Order order1 = new Order(customer1, List.of(orderItem1)); // Order 1
        Order order2 = new Order(customer2, List.of(orderItem2)); // Order 2
        Order order3 = new Order(customer1, List.of(orderItem3)); // Order 3

        // Return the list of orders
        return List.of(order1, order2, order3);
    }

    @FXML
    public void handleAddOrder(ActionEvent event) throws IOException {
        // Logic to add a new order
        System.out.println("Add Order clicked");
        // You can load a new FXML for adding orders or open a dialog here
    }

    @FXML
    public void handleEditOrder(ActionEvent event) throws IOException {
        // Logic to edit an existing order
        System.out.println("Edit Order clicked");
        // You can implement functionality to edit the selected order
    }

    @FXML
    public void handleDeleteOrder(ActionEvent event) throws IOException {
        // Logic to delete an existing order
        System.out.println("Delete Order clicked");
        // You can implement functionality to delete the selected order
    }
}
