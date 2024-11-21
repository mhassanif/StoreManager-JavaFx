package com.storemanager.controlers;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.items.Product;
import com.storemanager.db.DBconnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerProductsController {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TilePane productGrid;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button addAllToCartButton;

    private ShoppingCart activeCart = new ShoppingCart(1); // Example cart ID

    // Initialize method to load categories and products
    public void initialize() {
        loadCategories();
        loadRandomizedProducts();
        updateTotalAmount(); // Update total amount on startup
    }

    // Load categories into the ComboBox
    private void loadCategories() {
        try (Connection connection = DBconnector.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT name FROM CATEGORY");
            categoryComboBox.getItems().add(""); // Add an empty option to allow unselection
            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load randomized products
    private void loadRandomizedProducts() {
        try (Connection connection = DBconnector.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT * FROM PRODUCT ORDER BY NEWID()");
            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("brand"),
                        rs.getString("url"),
                        null, // Category can be fetched later if needed
                        rs.getString("description")
                );
                products.add(product);
            }

            displayProducts(products);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Display products in the grid
    private void displayProducts(List<Product> products) {
        productGrid.getChildren().clear();

        for (Product product : products) {
            VBox productBox = createProductBox(product);
            productGrid.getChildren().add(productBox);
        }
    }

    // Create a VBox for each product
    private VBox createProductBox(Product product) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1;");

        ImageView productImage = new ImageView();
        productImage.setFitWidth(150);
        productImage.setFitHeight(150);

        try {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                productImage.setImage(new Image(product.getImageUrl(), true));
            } else {
                throw new IllegalArgumentException("Missing Image URL");
            }
        } catch (RuntimeException e) {
            productImage.setImage(new Image("file:/D:/SDA PROJECT/StoreManager/src/main/resources/images/placeholder.png"));
        }

        Label nameLabel = new Label(product.getName());
        Label priceLabel = new Label("$" + product.getPrice());

        // Quantity Controls
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);

        Button cartButton = new Button("Add to Cart");
        cartButton.setOnAction(e -> handleCartButton(product, quantitySpinner, cartButton));

        box.getChildren().addAll(productImage, nameLabel, priceLabel, quantitySpinner, cartButton);
        return box;
    }

    // Handle adding/removing products from the cart
    private void handleCartButton(Product product, Spinner<Integer> quantitySpinner, Button cartButton) {
        int quantity = quantitySpinner.getValue();
        boolean inCart = activeCart.getItems().stream()
                .anyMatch(item -> item.getProduct().getId() == product.getId());

        if (inCart) {
            // Remove from cart
            activeCart.removeItem(new CartItem(product, quantity));
            cartButton.setText("Add to Cart");
        } else {
            // Add to cart
            activeCart.addItem(new CartItem(product, quantity));
            cartButton.setText("Remove from Cart");
        }

        updateTotalAmount();
    }

    // Update the total amount
    private void updateTotalAmount() {
        double total = activeCart.getTotalPrice();
        totalAmountLabel.setText("Total Amount: $" + String.format("%.2f", total));
    }

    // Filter products by category
    @FXML
    private void filterByCategory() {
        String selectedCategory = categoryComboBox.getValue();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            loadRandomizedProducts(); // Show all products if no category is selected
            return;
        }

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM PRODUCT WHERE category_id = (SELECT category_id FROM CATEGORY WHERE name = ?)")) {

            ps.setString(1, selectedCategory);
            ResultSet rs = ps.executeQuery();
            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("brand"),
                        rs.getString("url"),
                        null,
                        rs.getString("description")
                );
                products.add(product);
            }

            displayProducts(products);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add all displayed products to the cart
    @FXML
    private void addAllToCart() {
        for (CartItem item : activeCart.getItems()) {
            System.out.println("Finalized item in cart: " + item.getProduct().getName() + " x" + item.getQuantity());
        }
        // Redirect to Cart.fxml (Checkout Page)
        System.out.println("Redirecting to Cart.fxml for checkout...");
    }
}
