package com.storemanager.controlers;

import com.storemanager.db.DBconnector;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.items.Product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CategoriesController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, String> colBrand;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Void> colActions;

    private ShoppingCart shoppingCart;
    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        shoppingCart = new ShoppingCart(1); // Replace with the active cart ID
        setupTableColumns();
        loadCategories();
    }

    /**
     * Set up table columns.
     */
    private void setupTableColumns() {
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand()));
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnAdd = new Button("+");

            {
                btnAdd.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    shoppingCart.addItem(new com.storemanager.model.cart.CartItem(product, 1));
                });
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(btnAdd));
                }
            }
        });
    }

    /**
     * Load categories into the ComboBox.
     */
    private void loadCategories() {
        String query = "SELECT name FROM CATEGORY";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                categoryComboBox.getItems().add(resultSet.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load products for the selected category.
     */
    public void loadProducts() {
        products.clear();
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) return;

        String query = "SELECT * FROM PRODUCT WHERE category_id = (SELECT category_id FROM CATEGORY WHERE name = ?)";
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, selectedCategory);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt("product_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("brand"),
                        resultSet.getString("imageUrl"),
                        null, // Replace with category object if needed
                        resultSet.getString("description")
                );
                products.add(product);
            }

            productTable.setItems(products);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate back to the dashboard.
     */
    public void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) categoryComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
