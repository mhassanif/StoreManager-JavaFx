package com.storemanager.controlers;

import com.storemanager.communication.Feedback;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class StaffManageFeedbackController extends AdminBaseController {

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
    private Button btnApproveFeedback;

    @FXML
    private Button btnRejectFeedback;

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
        // Replace with actual database logic. Mock data used here for testing.
        allFeedback = fetchFeedbackFromDatabase();
    }

    @FXML
    public void handleApproveFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            System.out.println("Approving feedback: " + selectedFeedback.getComments());
            // Database logic to mark feedback as approved
        } else {
            showAlert("No Feedback Selected", "Please select a feedback to approve.");
        }
    }

    @FXML
    public void handleRejectFeedback() {
        Feedback selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            System.out.println("Rejecting feedback: " + selectedFeedback.getComments());
            // Database logic to mark feedback as rejected
        } else {
            showAlert("No Feedback Selected", "Please select a feedback to reject.");
        }
    }

    private List<Feedback> fetchFeedbackFromDatabase() {
        // Replace with actual database query logic
        return List.of(
                new Feedback(1, 101, "Great service, will come again!"),
                new Feedback(2, 102, "Product quality could be better."),
                new Feedback(3, 103, "Fast delivery, happy with the product.")
        );
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}