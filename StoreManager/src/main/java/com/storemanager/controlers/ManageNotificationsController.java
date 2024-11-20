package com.storemanager.controlers;

import com.storemanager.communication.Notification;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ManageNotificationsController extends AdminBaseController {

    @FXML
    private VBox contentArea;

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

    private List<Notification> allNotifications;  // To store all notifications for search and filter

    @FXML
    public void initialize() {
        // Initialize table columns with getter methods for Notification

        notificationIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        // Load notifications from the database or mock data
        loadNotifications();

        // Set the table data
        notificationsTable.getItems().setAll(allNotifications);
    }

    private void loadNotifications() {
        // In a real application, fetch the notification data from the database.
        // For now, we use a mock data generation method.
        allNotifications = fetchNotificationsFromDatabase();
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
            // Code to mark the selected notification as read
            selectedNotification.markAsRead();
            System.out.println("Notification marked as read: " + selectedNotification.getMessage());
            notificationsTable.refresh();
        } else {
            System.out.println("No notification selected to mark as read.");
        }
    }

    @FXML
    public void handleDeleteNotification() {
        Notification selectedNotification = notificationsTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            // Code to delete the selected notification from the database
            System.out.println("Delete Notification: " + selectedNotification.getMessage());
        } else {
            System.out.println("No notification selected to delete.");
        }
    }

    private List<Notification> fetchNotificationsFromDatabase() {
        // Mock data: Replace this with actual database call
        return List.of(
                new Notification(1, "Product 1 needs restocking", LocalDateTime.now(), "Unread"),
                new Notification(2, "Your order has been shipped", LocalDateTime.now().minusDays(1), "Read"),
                new Notification(3, "Product 2 has been added", LocalDateTime.now().minusHours(3), "Unread")
        );
    }
}
