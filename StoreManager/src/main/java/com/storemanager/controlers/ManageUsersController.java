package com.storemanager.controlers;

import com.storemanager.dao.CustomerDAO;
import com.storemanager.dao.StaffDAO;
import com.storemanager.dao.UserDAO;
import com.storemanager.model.users.Customer;
import com.storemanager.model.users.WarehouseStaff;
import com.storemanager.model.users.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

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
        List<User> users = UserDAO.getAllUsers();
        userTable.getItems().setAll(users);
    }

    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText();
        if (searchQuery.isEmpty()) {
            loadUsers();
        } else {
            List<User> searchResults = UserDAO.searchUsers(searchQuery);
            userTable.getItems().setAll(searchResults);
        }
    }

    @FXML
    private void handleDeleteUser(User user) {
        if (user instanceof Customer) {
            CustomerDAO.deleteCustomerByUserId(user.getId());
        } else if (user instanceof WarehouseStaff) {
            StaffDAO.deleteStaffByUserId(user.getId());
        } else {
            showAlert("Delete Not Allowed", "Admin and Manager accounts cannot be deleted.");
        }
        loadUsers(); // Refresh table
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
