package com.storemanager.controlers;

import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.StaffDAO;
import com.storemanager.dao.UserDAO;
import com.storemanager.model.users.Admin;
import com.storemanager.model.users.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
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
    private TableColumn<User, Void> actionColumn;

    @FXML
    private TextField searchField;

    private List<User> allUsers = new ArrayList<>(); // Holds all users loaded from the database

    @FXML
    private void initialize() {
        // Initialize table columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

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

    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText().toLowerCase();
        // Filter locally using the in-memory list
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(searchQuery) ||
                        user.getEmail().toLowerCase().contains(searchQuery) ||
                        user.getRole().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());
        // Update the table with the filtered results
        userTable.getItems().setAll(filteredUsers);
    }

    @FXML
    private void handleDeleteUser(User user) {
        String position = null;

        if (!"Customer".equalsIgnoreCase(user.getRole())) {
            // Fetch the position of the staff
            position = StaffDAO.getStaffPositionByUserId(user.getId());
        }

        // Check if the user is an Admin or Manager based on the position
        if ("Admin".equalsIgnoreCase(position) || "Manager".equalsIgnoreCase(position)) {
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

        // Proceed with the deletion
        boolean deleteSuccessful;
        if ("Customer".equalsIgnoreCase(user.getRole())) {
            deleteSuccessful = CustomerDAO.deleteCustomerByUserId(user.getId());
        } else {
            deleteSuccessful = StaffDAO.deleteStaffByUserId(user.getId());
        }

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
