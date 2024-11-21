package com.storemanager.controlers;

import com.storemanager.communication.Notification;
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

public class ManageNotificationsController extends AdminBaseController {

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

        // Load notifications
        loadNotifications();

        // Set the table data
        notificationsTable.getItems().setAll(allNotifications);
    }

    private void loadNotifications() {
        // Mock data: Replace with actual database logic
        allNotifications = new ArrayList<>();
        allNotifications.add(new Notification(1, "New product added to inventory", LocalDateTime.now(), "Unread"));
        allNotifications.add(new Notification(2, "Product restock required", LocalDateTime.now().minusDays(1), "Read"));
        allNotifications.add(new Notification(3, "Order shipped successfully", LocalDateTime.now().minusHours(5), "Unread"));
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
            selectedNotification.markAsRead();
            notificationsTable.refresh();
        } else {
            showAlert("No Notification Selected", "Please select a notification to mark as read.");
        }
    }

    @FXML
    public void handleDeleteNotification() {
        Notification selectedNotification = notificationsTable.getSelectionModel().getSelectedItem();
        if (selectedNotification != null) {
            allNotifications.remove(selectedNotification);
            notificationsTable.getItems().remove(selectedNotification);
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