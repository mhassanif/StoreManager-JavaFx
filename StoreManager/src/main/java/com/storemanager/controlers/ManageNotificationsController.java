package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.communication.Notification;
import com.storemanager.dao.NotificationDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageNotificationsController {

    @FXML
    private VBox mainVBox;

    @FXML
    private TextField searchField;

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
    private Button btnMarkAsRead, btnDeleteNotification;

    private List<Notification> allNotifications; // List of all notifications


    @FXML
    public void initialize() {
        // Set up table columns
        notificationIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        // Load notifications from the database
        loadNotifications();

        // Set the table data
        notificationsTable.getItems().setAll(allNotifications);
    }

    private void loadNotifications() {
        // Use NotificationDAO to fetch notifications for the current user
        allNotifications = NotificationDAO.getNotificationsForUser(CurrentUser.getInstance().getUser().getId());
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
            // Mark the notification as read in the database
            boolean success = NotificationDAO.markNotificationAsRead(selectedNotification.getId(), CurrentUser.getInstance().getUser().getId());
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
            // Delete the notification from the database
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