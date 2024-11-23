package com.storemanager.controlers;

import com.storemanager.model.users.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

public class ManageUsersController {

    @FXML
    private TableView<Customer> userTable;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    @FXML
    private TableColumn<Customer, String> roleColumn;

    @FXML
    private TableColumn<Customer, Void> actionColumn;

    @FXML
    private TextField searchField;

    private Customer selectedCustomer;

    @FXML
    private void initialize() {
        // Initialize the table columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        // Add "Delete" button to the action column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, deleteButton));
                    deleteButton.setOnAction(event -> handleDeleteCustomer());
                }
            }
        });

        // Load customers into the table
        loadCustomers();
    }

    private void loadCustomers() {
        // Mock some customers (replace with actual data retrieval later)
        List<Customer> customers = List.of(
                new Customer(1, 2, "John Doe", "john.doe@example.com", "password123", "123 Main St", "555-1234"),
                new Customer(3, 4, "Jane Smith", "jane.smith@example.com", "password123", "456 Elm St", "555-5678")
        );
        userTable.getItems().setAll(customers);
    }

    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText();
        // Search functionality (mocked for now)
        List<Customer> searchResults = List.of(); // Replace with actual search logic
        userTable.getItems().setAll(searchResults);
    }

    @FXML
    private void handleDeleteCustomer() {
        selectedCustomer = userTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            System.out.println("Delete Customer: " + selectedCustomer.getUsername());
            // Delete customer logic here
            loadCustomers(); // Refresh table after deletion
        } else {
            showAlert("No Customer Selected", "Please select a customer to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
