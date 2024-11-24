package com.storemanager.controlers;

import com.storemanager.model.items.InventoryProduct;
import com.storemanager.model.users.WarehouseStaff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    private WarehouseStaff warehouseStaff;

    /**
     * Set the WarehouseStaff instance for this controller.
     *
     * @param warehouseStaff The logged-in WarehouseStaff user.
     */
    public void setWarehouseStaff(WarehouseStaff warehouseStaff) {
        this.warehouseStaff = warehouseStaff;
        System.out.println("WarehouseInventoryController initialized for user: " + warehouseStaff.getUsername());
        loadInventory(); // Load inventory once the WarehouseStaff is set
    }

    @FXML
    public void initialize() {
        // Configure table columns
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockLevelColumn.setCellValueFactory(new PropertyValueFactory<>("stockLevel"));
        restockLevelColumn.setCellValueFactory(new PropertyValueFactory<>("restockLevel"));
        restockDateColumn.setCellValueFactory(new PropertyValueFactory<>("restockDate"));
    }

    private void loadInventory() {
        if (warehouseStaff == null) {
            System.err.println("WarehouseStaff is not set. Cannot load inventory.");
            return;
        }

        System.out.println("Loading inventory for warehouse staff: " + warehouseStaff.getUsername());

        // Fetch inventory from the WarehouseStaff's method
        List<InventoryProduct> inventory = warehouseStaff.viewInventory();
        ObservableList<InventoryProduct> inventoryData = FXCollections.observableArrayList(inventory);

        // Populate the table
        inventoryTable.setItems(inventoryData);

        System.out.println("Inventory loaded with " + inventory.size() + " items.");
    }
}
