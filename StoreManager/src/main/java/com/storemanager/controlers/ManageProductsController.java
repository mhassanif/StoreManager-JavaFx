package com.storemanager.controlers;

import com.storemanager.dao.InventoryDAO;
import com.storemanager.dao.ProductDAO;
import com.storemanager.model.items.InventoryProduct;
import com.storemanager.model.items.Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class ManageProductsController {

    @FXML
    private VBox contentArea;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> brandFilter;

    @FXML
    private TableView<InventoryProduct> productsTable;

    @FXML
    private TableColumn<InventoryProduct, Integer> productIdColumn;

    @FXML
    private TableColumn<InventoryProduct, String> productNameColumn;

    @FXML
    private TableColumn<InventoryProduct, String> brandColumn;

    @FXML
    private TableColumn<InventoryProduct, Double> priceColumn;

    @FXML
    private TableColumn<InventoryProduct, Integer> stockLevelColumn;

    @FXML
    private TableColumn<InventoryProduct, Integer> restockLevelColumn;

    @FXML
    private TableColumn<InventoryProduct, String> restockDateColumn;

    @FXML
    private Button btnAddProduct, btnDeleteProduct, btnSetRestockLevel;

    private List<InventoryProduct> allInventoryProducts;

    @FXML
    public void initialize() {
        // Initialize table columns
        productIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProduct().getId()).asObject());
        productNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));
        brandColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getBrand()));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());
        stockLevelColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());
        restockLevelColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRestockLevel()).asObject());
        restockDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRestockDate()));

        // Load products from the database
        loadProducts();

        // Populate table
        productsTable.getItems().setAll(allInventoryProducts);

        // Populate brand filter dropdown
        brandFilter.getItems().addAll(allInventoryProducts.stream()
                .map(inventoryProduct -> inventoryProduct.getProduct().getBrand())
                .distinct()
                .collect(Collectors.toList()));
    }

    private void loadProducts() {
        // Fetch products and their inventory from the database
        List<Product> products = ProductDAO.searchProducts(""); // Fetch all products
        allInventoryProducts = products.stream()
                .map(product -> new InventoryDAO().getInventoryByProductId(product.getId()))
                .collect(Collectors.toList());
    }

    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<InventoryProduct> filteredProducts = allInventoryProducts.stream()
                .filter(product -> product.getProduct().getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        productsTable.getItems().setAll(filteredProducts);
    }

    @FXML
    public void handleFilterBrand() {
        String selectedBrand = brandFilter.getValue();
        if (selectedBrand != null) {
            List<InventoryProduct> filteredByBrand = allInventoryProducts.stream()
                    .filter(product -> product.getProduct().getBrand().equals(selectedBrand))
                    .collect(Collectors.toList());
            productsTable.getItems().setAll(filteredByBrand);
        } else {
            productsTable.getItems().setAll(allInventoryProducts);
        }
    }

    @FXML
    public void handleAddProduct() {
        System.out.println("Add Product clicked");
        // Open a new form for adding a product
    }

    @FXML
    public void handleDeleteProduct() {
        InventoryProduct selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            System.out.println("Delete Product: " + selectedProduct.getProduct().getName());
            // Delete product from the database
            boolean success = ProductDAO.deleteProduct(selectedProduct.getProduct().getId());
            if (success) {
                // Remove from table and refresh
                allInventoryProducts.remove(selectedProduct);
                productsTable.getItems().setAll(allInventoryProducts);
            } else {
                System.out.println("Failed to delete the product.");
            }
        } else {
            System.out.println("No product selected for deletion");
        }
    }

    @FXML
    public void handleSetRestockLevel() {
        InventoryProduct selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            System.out.println("Set Restock Level for Product: " + selectedProduct.getProduct().getName());

            // Create a dialog to input the restock level
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Set Restock Level");
            dialog.setHeaderText("Enter the new restock level for " + selectedProduct.getProduct().getName());
            dialog.setContentText("Restock Level:");

            // Show dialog and wait for input
            dialog.showAndWait().ifPresent(restockLevelInput -> {
                try {
                    int restockLevel = Integer.parseInt(restockLevelInput);  // Parse the input to an integer
                    // Update restock level in the database using InventoryDAO
                    boolean success = InventoryDAO.setRestockLevel(selectedProduct.getProduct().getId(), restockLevel);
                    if (success) {
                        // Refresh the inventory product list if the update was successful
                        selectedProduct.setRestockLevel(restockLevel);  // Update in the table view
                        productsTable.refresh();  // Refresh the table to reflect the changes
                        System.out.println("Restock level updated successfully.");
                    } else {
                        System.out.println("Failed to update restock level.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                }
            });
        } else {
            System.out.println("No product selected to set restock level");
        }
    }

}
