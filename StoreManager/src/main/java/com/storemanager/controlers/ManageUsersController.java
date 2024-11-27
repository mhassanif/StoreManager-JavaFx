package com.storemanager.controlers;

import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.StaffDAO;
import com.storemanager.dao.UserDAO;
import com.storemanager.model.users.Customer;
import com.storemanager.model.users.User;
import com.storemanager.model.users.WarehouseStaff;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageUsersController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> userIdColumn;

    @FXML
    private TableColumn<User, String> customerStaffIdColumn;

    @FXML
    private TableColumn<User, Void> actionColumn;

    @FXML
    private TextField searchField;

    private List<User> allUsers = new ArrayList<>(); // Holds all users loaded from the database

    @FXML
    private void initialize() {
        // Initialize table columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getRole(cellData.getValue())));
        userIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        // Add action buttons to the table
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, deleteButton));
                    deleteButton.setOnAction(event -> handleDeleteUser(getTableView().getItems().get(getIndex())));
                }
            }
        });

        // Load initial data
        loadUsers();
    }

    private void loadUsers() {
        // Fetch all users from the database
        allUsers = UserDAO.getAllUsers();
        // Display all users in the table
        userTable.getItems().setAll(allUsers);
    }

    private String getRole(User user) {
        // Check the user_id to determine the role
        if (user.getId() == 1 && "Staff".equalsIgnoreCase(user.getRole())) {
            return "Admin"; // user_id 1 is Admin
        } else if (user.getId() == 2 && "Staff".equalsIgnoreCase(user.getRole())) {
            return "Manager"; // user_id 2 is Manager
        } else if ("Staff".equalsIgnoreCase(user.getRole())) {
            return "WarehouseStaff"; // Other staff members
        }
        return "Customer"; // Default to Customer
    }

    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText().toLowerCase();
        // Filter locally using the in-memory list
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(searchQuery) ||
                        user.getEmail().toLowerCase().contains(searchQuery) ||
                        getRole(user).toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());
        // Update the table with the filtered results
        userTable.getItems().setAll(filteredUsers);
    }

    @FXML
    private void handleDeleteUser(User user) {
        // Check if the user is an Admin or Manager, and prevent deletion
        if ("Admin".equalsIgnoreCase(getRole(user)) || "Manager".equalsIgnoreCase(getRole(user))) {
            showAlert("Delete Not Allowed", "Admin and Manager accounts cannot be deleted.");
            return;
        }

        // Show a confirmation dialog to confirm the deletion
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText("Are you sure you want to delete this user?");
        confirmationAlert.setContentText("This action cannot be undone.");

        // Wait for user response
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            // User chose to cancel the deletion
            return;
        }

        if(user.getRole().equalsIgnoreCase("Customer")){
            CustomerDAO.deleteCustomer(CustomerDAO.getCustomerByUserId(user.getId()).getCustomerId());
        }
        else{
            StaffDAO.deleteStaff(StaffDAO.getStaffIdByUserId(user.getId()));
        }

        // Proceed with the deletion (call the appropriate DAO method)
        boolean deleteSuccessful = UserDAO.deleteUser(user.getId());

        if (deleteSuccessful) {
            // Remove the user from the local list
            allUsers.remove(user);
            // Refresh the table to reflect the change
            userTable.getItems().remove(user);
        } else {
            showAlert("Error", "Failed to delete the user.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
