package com.storemanager.controlers;

import com.storemanager.model.items.InventoryProduct;
import com.storemanager.model.items.Product;
import com.storemanager.model.items.Category;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
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

        // Load mock data
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
        // Mock data for testing
        allInventoryProducts = fetchInventoryProductsFromDatabase();
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
        try {
            // Load the FXML for the CreateProduct page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/CreateProduct.fxml"));
            Parent createProductRoot = loader.load();

            // Pass the current stage as a reference to allow navigation back
            CreateProductController createProductController = loader.getController();
            createProductController.setPreviousStage((Stage) btnAddProduct.getScene().getWindow());

            // Set up a new stage for the CreateProduct page
            Stage stage = new Stage();
            stage.setTitle("Create New Product");
            stage.setScene(new Scene(createProductRoot));
            stage.show();

            // Close the current ManageProducts stage (optional if you want only one window open at a time)
            btnAddProduct.getScene().getWindow().hide();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load CreateProduct page.", e.getMessage());
        }
    }



    @FXML
    public void handleDeleteProduct() {
        InventoryProduct selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            System.out.println("Delete Product: " + selectedProduct.getProduct().getName());
            // Remove from database and refresh table
            allInventoryProducts.remove(selectedProduct);
            productsTable.getItems().setAll(allInventoryProducts);
        } else {
            System.out.println("No product selected for deletion");
        }
    }

    @FXML
    public void handleSetRestockLevel() {
        InventoryProduct selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            System.out.println("Set Restock Level for Product: " + selectedProduct.getProduct().getName());
            // Open dialog to set restock level
        } else {
            System.out.println("No product selected to set restock level");
        }
    }

    private List<InventoryProduct> fetchInventoryProductsFromDatabase() {
        // Mock data for testing
        Category categoryA = new Category(1, "Category A");
        Category categoryB = new Category(2, "Category B");

        Product product1 = new Product(1, "Product A", 100.0, "Brand A", null, categoryA, "Description A");
        Product product2 = new Product(2, "Product B", 150.0, "Brand B", null, categoryB, "Description B");
        Product product3 = new Product(3, "Product C", 200.0, "Brand A", null, categoryA, "Description C");

        return List.of(
                new InventoryProduct(product1, 50, 20, "2024-11-30"),
                new InventoryProduct(product2, 100, 50, "2024-12-15"),
                new InventoryProduct(product3, 30, 10, "2024-12-20")
        );
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
