package com.storemanager.controlers;

import com.storemanager.dao.InventoryDAO;
import com.storemanager.model.items.InventoryProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class WarehouseInventoryController {

    @FXML
    private TableView<InventoryProduct> inventoryTable;

    @FXML
    private TableColumn<InventoryProduct, String> productNameColumn;

    @FXML
    private TableColumn<InventoryProduct, Double> priceColumn;

    @FXML
    private TableColumn<InventoryProduct, Integer> stockLevelColumn;

    @FXML
    private TableColumn<InventoryProduct, Integer> restockLevelColumn;

    @FXML
    private TableColumn<InventoryProduct, String> restockDateColumn;

    @FXML
    private TextField stockLevelField;

    @FXML
    private TextField restockLevelField;

    @FXML
    private Label statusMessage;

    private ObservableList<InventoryProduct> inventoryData;

    @FXML
    public void initialize() {
        // Configure table columns
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockLevelColumn.setCellValueFactory(new PropertyValueFactory<>("stockLevel"));
        restockLevelColumn.setCellValueFactory(new PropertyValueFactory<>("restockLevel"));
        restockDateColumn.setCellValueFactory(new PropertyValueFactory<>("restockDate"));

        loadInventory();
    }

    private void loadInventory() {
        // Fetch inventory from the DAO
        List<InventoryProduct> inventory = InventoryDAO.getAllInventory();
        inventoryData = FXCollections.observableArrayList(inventory);

        // Populate the table
        inventoryTable.setItems(inventoryData);

        System.out.println("Inventory loaded with " + inventory.size() + " items.");
    }

    @FXML
    private void handleRestockProduct() {
        // Get the selected product
        InventoryProduct selectedProduct = inventoryTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            statusMessage.setText("Please select a product to update.");
            statusMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Get the new stock level and restock level from the input fields
            int newStockLevel = Integer.parseInt(stockLevelField.getText());
            int newRestockLevel = Integer.parseInt(restockLevelField.getText());

            // Update the stock quantity
            boolean stockUpdated = InventoryDAO.updateStockQuantity(selectedProduct.getProduct().getId(), newStockLevel);

            // Update the restock level
            boolean restockUpdated = InventoryDAO.setRestockLevel(selectedProduct.getProduct().getId(), newRestockLevel);

            if (stockUpdated && restockUpdated) {
                // Reflect changes in the table
                selectedProduct.setStockLevel(newStockLevel);
                selectedProduct.setRestockLevel(newRestockLevel);
                inventoryTable.refresh();

                statusMessage.setText("Product updated successfully.");
                statusMessage.setStyle("-fx-text-fill: green;");
            } else {
                statusMessage.setText("Failed to update product.");
                statusMessage.setStyle("-fx-text-fill: red;");
            }
        } catch (NumberFormatException e) {
            statusMessage.setText("Invalid input. Please enter numeric values.");
            statusMessage.setStyle("-fx-text-fill: red;");
        }
    }
}
