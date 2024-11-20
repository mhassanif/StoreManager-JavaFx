package com.storemanager.controlers;

import com.storemanager.model.items.InventoryProduct;
import com.storemanager.model.items.Product;
import com.storemanager.model.items.Category;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class ManageProductsController extends AdminBaseController {

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

    private List<InventoryProduct> allInventoryProducts;  // To store all inventory products for search and filter

    @FXML
    public void initialize() {
        // Initialize table columns with getter methods for InventoryProduct

        // For Integer columns (productId, stockLevel, restockLevel)
        productIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProduct().getId()).asObject());

        // For String columns (productName, brand)
        productNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName()));

        brandColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getBrand()));

        // For Double columns (price)
        priceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject());

        // For Integer columns (stockLevel, restockLevel)
        stockLevelColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getStockLevel()).asObject());

        restockLevelColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRestockLevel()).asObject());

        // For String column (restockDate)
        restockDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRestockDate()));

        // Load the products from the database or mock data
        loadProducts();

        // Set the table data
        productsTable.getItems().setAll(allInventoryProducts);

        // Populate the brand filter combobox (example brands here)
        brandFilter.getItems().addAll("Brand A", "Brand B", "Brand C");
    }

    private void loadProducts() {
        // In a real application, fetch the product data from the database.
        // For now, we use a mock data generation method.
        allInventoryProducts = fetchInventoryProductsFromDatabase();
    }

    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<InventoryProduct> filteredProducts = allInventoryProducts.stream()
                .filter(inventoryProduct -> inventoryProduct.getProduct().getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        productsTable.getItems().setAll(filteredProducts);
    }

    @FXML
    public void handleFilterBrand() {
        String selectedBrand = brandFilter.getValue();
        if (selectedBrand != null) {
            List<InventoryProduct> filteredByBrand = allInventoryProducts.stream()
                    .filter(inventoryProduct -> inventoryProduct.getProduct().getBrand().equals(selectedBrand))
                    .collect(Collectors.toList());
            productsTable.getItems().setAll(filteredByBrand);
        } else {
            productsTable.getItems().setAll(allInventoryProducts);
        }
    }

    @FXML
    public void handleAddProduct() {
        // Code to add a new product (open a new form to add product details)
        System.out.println("Add Product button clicked.");
    }

    @FXML
    public void handleDeleteProduct() {
        InventoryProduct selectedInventoryProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedInventoryProduct != null) {
            // Code to delete the selected product from the database
            System.out.println("Delete Product: " + selectedInventoryProduct.getProduct().getName());
        } else {
            System.out.println("No product selected to delete.");
        }
    }

    @FXML
    public void handleSetRestockLevel() {
        InventoryProduct selectedInventoryProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedInventoryProduct != null) {
            // Code to set the restock level of the selected product
            System.out.println("Set Restock Level for Product: " + selectedInventoryProduct.getProduct().getName());
        } else {
            System.out.println("No product selected to set restock level.");
        }
    }

    private List<InventoryProduct> fetchInventoryProductsFromDatabase() {     //testing
        // Mock data: Replace this with actual database call
        Category category1 = new Category(1, "Category A");
        Category category2 = new Category(2, "Category B");

        Product product1 = new Product(1, "Product 1", 100.0, "Brand A", "image1.jpg", category1, "Description of Product 1");
        Product product2 = new Product(2, "Product 2", 150.0, "Brand B", "image2.jpg", category2, "Description of Product 2");
        Product product3 = new Product(3, "Product 3", 200.0, "Brand A", "image3.jpg", category1, "Description of Product 3");

        return List.of(
                new InventoryProduct(product1, 100, 50, "2024-12-01"),
                new InventoryProduct(product2, 200, 75, "2024-12-05"),
                new InventoryProduct(product3, 50, 30, "2024-12-10")
        );
    }
}
