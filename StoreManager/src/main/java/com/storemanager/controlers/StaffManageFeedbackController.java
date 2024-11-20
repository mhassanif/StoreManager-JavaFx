package com.storemanager.controlers;

import com.storemanager.communication.Feedback;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class StaffManageFeedbackController extends AdminBaseController {

    @FXML
    private VBox contentArea;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Feedback> feedbackTable;

    @FXML
    private TableColumn<Feedback, Integer> feedbackIdColumn;

    @FXML
    private TableColumn<Feedback, Integer> customerIdColumn;

    @FXML
    private TableColumn<Feedback, String> commentsColumn;

    @FXML
    private Button btnApproveFeedback;

    @FXML
    private Button btnRejectFeedback;

    private List<Feedback> allFeedback;  // To store all feedback for search and filter

    @FXML
    public void initialize() {
        // Initialize table columns with getter methods for Feedback
        feedbackIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        customerIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
        commentsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComments()));

        // Load feedback from the database or mock data
        loadFeedback();

        // Set the table data
        feedbackTable.getItems().setAll(allFeedback);
    }

    private void loadFeedback() {
        // In a real application, fetch the feedback data from the database.
        // For now, we use a mock data generation method.
        allFeedback = fetchFeedbackFromDatabase();
    }

    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Feedback> filteredFeedback = allFeedback.stream()
                .filter(feedback -> feedback.getComments().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        feedbackTable.getItems().setAll(filteredFeedback);
    }

    @FXML
    public void handleApproveFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            // Code to approve the feedback in the database
            System.out.println("Approve Feedback: " + selectedFeedback.getComments());
            // Implement database update here (e.g., change status to 'approved')
        } else {
            System.out.println("No feedback selected to approve.");
        }
    }

    @FXML
    public void handleRejectFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            // Code to reject the feedback in the database
            System.out.println("Reject Feedback: " + selectedFeedback.getComments());
            // Implement database update here (e.g., change status to 'rejected')
        } else {
            System.out.println("No feedback selected to reject.");
        }
    }

    @FXML
    public void handleDeleteFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            // Code to delete the selected feedback from the database
            System.out.println("Delete Feedback: " + selectedFeedback.getComments());
            // Implement database deletion here
        } else {
            System.out.println("No feedback selected to delete.");
        }
    }

    private List<Feedback> fetchFeedbackFromDatabase() {
        // Mock data: Replace this with actual database call
        return List.of(
                new Feedback(1, 101, "Great service, will come again!"),
                new Feedback(2, 102, "Product quality could be better."),
                new Feedback(3, 103, "Fast delivery, happy with the product.")
        );
    }
}
