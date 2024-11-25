package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.CategoryDAO;
import com.storemanager.dao.ProductDAO;
import com.storemanager.model.cart.CartItem;
import com.storemanager.model.items.Category;
import com.storemanager.model.items.Product;
import com.storemanager.model.users.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

public class CustomerProductsController {

    @FXML
    private ComboBox<Category> categoryComboBox; // Updated to hold Category objects

    @FXML
    private TilePane productGrid;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button addAllToCartButton;

    private Customer customer = (Customer) CurrentUser.getInstance().getUser(); // Get current customer

    // Initialize method to load categories and products
    public void initialize() {
        loadCategories();
        loadRandomizedProducts();
        updateTotalAmount(); // Update total amount on startup
    }

    // Load categories into the ComboBox using CategoryDAO
    private void loadCategories() {
        List<Category> categories = CategoryDAO.getAllCategories();
        categoryComboBox.getItems().add(null); // Add an empty option to allow unselection
        categoryComboBox.getItems().addAll(categories);

        categoryComboBox.setConverter(new StringConverter<>() { // Show category names
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "All Categories";
            }

            @Override
            public Category fromString(String string) {
                return categories.stream().filter(c -> c.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }

    // Load randomized products using ProductDAO
    private void loadRandomizedProducts() {
        List<Product> products = ProductDAO.getAllProducts(); // DAO call to fetch all products
        displayProducts(products);
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
        boolean inCart = customer.getShoppingCart().getItems().stream()
                .anyMatch(item -> item.getProduct().getId() == product.getId());

        if (inCart) {
            // Remove from cart
            customer.removeFromCart(new CartItem(product, quantity));
            cartButton.setText("Add to Cart");
        } else {
            // Add to cart
            customer.addToCart(new CartItem(product, quantity));
            cartButton.setText("Remove from Cart");
        }

        updateTotalAmount();
    }

    // Update the total amount
    private void updateTotalAmount() {
        double total = customer.getShoppingCart().getTotalPrice();
        totalAmountLabel.setText("Total Amount: $" + String.format("%.2f", total));
    }

    // Filter products by category using ProductDAO
    @FXML
    private void filterByCategory() {
        Category selectedCategory = categoryComboBox.getValue();

        if (selectedCategory == null) {
            loadRandomizedProducts(); // Show all products if no category is selected
            return;
        }

        List<Product> products = ProductDAO.getProductsByCategoryId(selectedCategory.getId()); // DAO method for filtering
        displayProducts(products);
    }

    // Add all displayed products to the cart
    @FXML
    private void addAllToCart() {
        for (CartItem item : customer.getShoppingCart().getItems()) {
            System.out.println("Finalized item in cart: " + item.getProduct().getName() + " x" + item.getQuantity());
        }
        // Redirect to Cart.fxml (Checkout Page)
        System.out.println("Redirecting to Cart.fxml for checkout...");
    }
}
