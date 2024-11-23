package com.storemanager.controlers;

import com.storemanager.dao.CategoryDAO;
import com.storemanager.dao.ProductDAO;
import com.storemanager.model.items.Category;
import com.storemanager.model.items.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CreateProductController {

    @FXML private TextField productNameField;
    @FXML private TextField priceField;
    @FXML private TextField brandField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    private Stage previousStage;
    @FXML
    private void initialize() {
        // Load categories into ComboBox
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Category> categories = CategoryDAO.getAllCategories(); // Fetch categories from the database
            ObservableList<Category> categoryList = FXCollections.observableArrayList(categories);
            categoryComboBox.setItems(categoryList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load categories.", e.getMessage());
        }
    }

    @FXML
    private void handleSaveProduct() {
        try {
            // Validate inputs
            String name = productNameField.getText().trim();
            String priceText = priceField.getText().trim();
            String brand = brandField.getText().trim();
            Category category = categoryComboBox.getValue();
            String description = descriptionArea.getText().trim();
            String imageUrl = imageUrlField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || brand.isEmpty() || category == null || imageUrl.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price < 0) throw new NumberFormatException("Price cannot be negative.");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid price.");
                return;
            }

            // Create Product object
            Product newProduct = new Product(
                    0, // Assuming ID is auto-generated
                    name,
                    price,
                    brand,
                    imageUrl,
                    category,
                    description
            );

            // Save the product
            boolean success = ProductDAO.createProduct(newProduct);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product created successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create product.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred.", e.getMessage());
        }
    }

    public void setPreviousStage(Stage stage) {
        this.previousStage = stage;
    }

    @FXML
    public void handleCancel() {
        if (previousStage != null) {
            previousStage.show(); // Show the ManageProducts window
            // Close the current Create Product window
            Stage createProductStage = (Stage) btnCancel.getScene().getWindow();
            createProductStage.close();
        }
    }


    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText) {
        showAlert(alertType, title, headerText, null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
