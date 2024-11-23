package com.storemanager.controlers;

import com.storemanager.communication.Feedback;
import com.storemanager.dao.FeedbackDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class StaffManageFeedbackController {

    @FXML
    private VBox mainVBox;

    @FXML
    private TableView<Feedback> feedbackTable;

    @FXML
    private TableColumn<Feedback, Integer> feedbackIdColumn;

    @FXML
    private TableColumn<Feedback, Integer> customerIdColumn;

    @FXML
    private TableColumn<Feedback, String> commentsColumn;

    @FXML
    private Button btnDeleteFeedback;

    private List<Feedback> allFeedback; // To store all feedback for searching and filtering

    @FXML
    public void initialize() {
        // Initialize table columns
        feedbackIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        customerIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
        commentsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComments()));

        // Load feedback into the table
        loadFeedback();
        feedbackTable.getItems().setAll(allFeedback);
    }

    private void loadFeedback() {
        // Fetch all feedback from the database using FeedbackDAO
        allFeedback = FeedbackDAO.getFeedbackByCustomerId(0);  // Passing 0 to fetch all feedback, adjust as needed
    }

    @FXML
    public void handleDeleteFeedback() {
        // Get the selected feedback
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            // Delete feedback from database
            boolean isDeleted = FeedbackDAO.deleteFeedback(selectedFeedback.getId());
            if (isDeleted) {
                // Remove from table
                allFeedback.remove(selectedFeedback);
                feedbackTable.getItems().setAll(allFeedback);
                showAlert("Success", "Feedback deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete feedback.");
            }
        } else {
            showAlert("No Feedback Selected", "Please select a feedback to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}