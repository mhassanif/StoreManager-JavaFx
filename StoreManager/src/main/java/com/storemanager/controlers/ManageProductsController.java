package com.storemanager.controlers;

import com.storemanager.dao.InventoryDAO;
import com.storemanager.dao.ProductDAO;
import com.storemanager.dao.InventoryProductDAO;
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

    // DAOs
    private ProductDAO productDAO;
    private InventoryProductDAO inventoryProductDAO;

    @FXML
    public void initialize() {
        // Initialize DAO instances
        productDAO = new ProductDAO();
        inventoryProductDAO = new InventoryProductDAO();

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

        // Load products from database
        loadProducts();

        // Populate brand filter dropdown
        brandFilter.getItems().addAll(allInventoryProducts.stream()
                .map(inventoryProduct -> inventoryProduct.getProduct().getBrand())
                .distinct()
                .collect(Collectors.toList()));
    }

    private void loadProducts() {
        // Fetch inventory products from the database using the DAO
        allInventoryProducts = InventoryDAO.getAllInventory();
        productsTable.getItems().setAll(allInventoryProducts);
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
        // Logic for adding a product (could open a new form or modal)
        System.out.println("Add Product clicked");
    }

    @FXML
    public void handleDeleteProduct() {
        InventoryProduct selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Delete from database using the DAO
            ProductDAO.deleteProduct(selectedProduct.getProduct().getId());
            allInventoryProducts.remove(selectedProduct);
            productsTable.getItems().setAll(allInventoryProducts);
            System.out.println("Deleted Product: " + selectedProduct.getProduct().getName());
        } else {
            System.out.println("No product selected for deletion");
        }
    }

    @FXML
    public void handleSetRestockLevel() {
        InventoryProduct selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Open dialog to set restock level or update via DAO
            System.out.println("Set Restock Level for Product: " + selectedProduct.getProduct().getName());
            // Example logic: update restock level in database
            selectedProduct.setRestockLevel(50);  // Update restock level
            InventoryDAO.setRestockLevel(selectedProduct.getProduct().getId(),selectedProduct.getRestockLevel());
            productsTable.refresh();  // Refresh table to reflect changes
        } else {
            System.out.println("No product selected to set restock level");
        }
    }
}
