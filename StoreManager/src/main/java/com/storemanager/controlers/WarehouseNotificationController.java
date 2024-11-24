package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.communication.Notification;
import com.storemanager.dao.NotificationDAO;
import com.storemanager.model.users.WarehouseStaff;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class WarehouseNotificationController {

    @FXML
    private TableView<Notification> notificationsTable;

    @FXML
    private TableColumn<Notification, Integer> notificationIdColumn;

    @FXML
    private TableColumn<Notification, String> messageColumn;

    @FXML
    private TableColumn<Notification, String> statusColumn;

    @FXML
    private TableColumn<Notification, String> dateColumn;

    @FXML
    private TextField searchField;

    private WarehouseStaff warehouseStaff;
    private List<Notification> allNotifications;

    public void setWarehouseStaff(WarehouseStaff warehouseStaff) {
        this.warehouseStaff = warehouseStaff;
        loadNotifications();
    }

    @FXML
    public void initialize() {
        // Set up table columns
        notificationIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        // Set default table data if warehouseStaff is already set
        if (warehouseStaff != null) {
            loadNotifications();
        }
    }

    private void loadNotifications() {
        if (warehouseStaff != null) {
            // Fetch notifications for the current warehouse staff
            allNotifications = NotificationDAO.getNotificationsForUser(warehouseStaff.getId());
            notificationsTable.getItems().setAll(allNotifications);
        } else {
            System.err.println("WarehouseStaff object is null. Cannot load notifications.");
        }
    }

    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Notification> filteredNotifications = allNotifications.stream()
                .filter(notification -> notification.getMessage().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        notificationsTable.getItems().setAll(filteredNotifications);
    }

    @FXML
    public void handleMarkAsRead() {
        Notification selectedNotification = notificationsTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            boolean success = NotificationDAO.markNotificationAsRead(selectedNotification.getId(), warehouseStaff.getId());
            if (success) {
                selectedNotification.setStatus("Read");
                notificationsTable.refresh();
            } else {
                showAlert("Error", "Failed to mark notification as read.");
            }
        } else {
            showAlert("No Notification Selected", "Please select a notification to mark as read.");
        }
    }

    @FXML
    public void handleDeleteNotification() {
        Notification selectedNotification = notificationsTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            boolean success = NotificationDAO.deleteNotification(selectedNotification.getId());
            if (success) {
                allNotifications.remove(selectedNotification);
                notificationsTable.getItems().remove(selectedNotification);
            } else {
                showAlert("Error", "Failed to delete notification.");
            }
        } else {
            showAlert("No Notification Selected", "Please select a notification to delete.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
