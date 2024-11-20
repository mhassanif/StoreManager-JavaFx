package com.storemanager.controlers;

import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, String> colProductBrand;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colTotal;
    @FXML private TableColumn<CartItem, Void> colActions;
    @FXML private Label totalAmountLabel;

    private ShoppingCart shoppingCart;

    @FXML
    public void initialize() {
        // Initialize ShoppingCart with a cart ID (this can be dynamic or fetched from the database)
        shoppingCart = new ShoppingCart(1);

        // Set up table columns
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        colProductBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getBrand()));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().calculateSubtotal()).asObject());

        // Load items into the table
        loadCartItems();

        // Add action buttons for quantity adjustment
        addActionButtons();
    }

    /**
     * Load cart items into the TableView from the ShoppingCart.
     */
    private void loadCartItems() {
        ObservableList<CartItem> cartItems = FXCollections.observableArrayList(shoppingCart.getItems());
        cartTable.setItems(cartItems);
        updateTotalAmount();
    }

    /**
     * Add action buttons (+ and -) to the table for quantity adjustment.
     */
    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnAdd = new Button("+");
            private final Button btnSubtract = new Button("-");

            {
                btnAdd.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    item.setQuantity(item.getQuantity() + 1);
                    shoppingCart.addItem(item); // Update the shopping cart
                    loadCartItems();           // Reload the table
                });

                btnSubtract.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        loadCartItems();       // Reload the table
                    } else {
                        shoppingCart.removeItem(item); // Remove item if quantity reaches 0
                        loadCartItems();               // Reload the table
                    }
                });
            }

            @Override
            protected void updateItem(Void unused, boolean empty) {
                super.updateItem(unused, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(5, btnAdd, btnSubtract);
                    setGraphic(hBox);
                }
            }
        });
    }

    /**
     * Clear all items from the shopping cart.
     */
    public void clearCart() {
        shoppingCart.clearCart();
        loadCartItems();
    }

    /**
     * Update the total price displayed in the label.
     */
    private void updateTotalAmount() {
        totalAmountLabel.setText("$" + String.format("%.2f", shoppingCart.getTotalPrice()));
    }

    /**
     * Placeholder for the checkout process.
     */
    public void proceedToCheckout() {
        System.out.println("Proceeding to checkout...");
        // Implement checkout logic, such as converting cart items to an order
    }
}
