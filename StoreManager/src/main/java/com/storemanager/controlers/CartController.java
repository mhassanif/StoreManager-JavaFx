package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.ShoppingCartDAO;
import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.items.Product;
import com.storemanager.model.users.Customer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

import java.util.Optional;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, String> colProductBrand;
    @FXML private TableColumn<CartItem, Integer> colQuantity;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colTotal;
    @FXML private TableColumn<CartItem, Void> colActions;
    @FXML private Label totalAmountLabel;
    @FXML private Button proceedToCheckoutButton; // Added reference to the button

    private ShoppingCart shoppingCart;
    private int currentUserId; // Holds the logged-in user ID

    @FXML
    public void initialize() {
        // Fetch the logged-in user's ID
        currentUserId = ((Customer) CurrentUser.getInstance().getUser()).getCustomerId();

        // Initialize ShoppingCart for the logged-in customer
        shoppingCart = ((Customer) CurrentUser.getInstance().getUser()).getShoppingCart();

        // Set up table columns
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        colProductBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getBrand()));
        colQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().calculateSubtotal()).asObject());

        // Load items into the table
        loadCartItems();

        // Add action buttons for quantity adjustment
        addActionButtons();

        // Disable the "Proceed to Checkout" button if the cart is empty
        updateProceedButtonState();
    }

    /**
     * Load cart items from the database into the TableView.
     */
    private void loadCartItems() {
        shoppingCart.getItems().clear();
        shoppingCart.loadItems();

        ObservableList<CartItem> cartItems = FXCollections.observableArrayList(shoppingCart.getItems());
        cartTable.setItems(cartItems);
        updateTotalAmount();

        // Update the state of the proceed button after loading items
        updateProceedButtonState();
    }

    /**
     * Add action buttons (+ and -) for quantity adjustment.
     */
    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnAdd = new Button("+");
            private final Button btnSubtract = new Button("-");

            {
                btnAdd.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    item.setQuantity(item.getQuantity() + 1);
                    updateCartItemInDB(item);
                    loadCartItems();
                });

                btnSubtract.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        updateCartItemInDB(item);
                        loadCartItems();
                    } else {
                        // Remove the item from the cart if quantity is 1
                        ShoppingCartDAO.removeItem(shoppingCart.getCartId(), item.getProduct().getId());
                        loadCartItems();
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
     * Update cart item quantity in the database.
     */
    private void updateCartItemInDB(CartItem item) {
        // Recalculate the total price for the cart item
        double totalPrice = item.calculateSubtotal();

        // Update the cart item in the database
        ShoppingCartDAO.updateCartItem(shoppingCart.getCartId(), item.getProduct().getId(), item.getQuantity(), totalPrice);
    }

    /**
     * Clear all items from the cart.
     */
    public void clearCart() {
        shoppingCart.clearCart();
        loadCartItems();
    }

    /**
     * Update the total price label.
     */
    private void updateTotalAmount() {
        totalAmountLabel.setText("$" + String.format("%.2f", shoppingCart.getTotalPrice()));
    }

    /**
     * Update the state of the "Proceed to Checkout" button based on cart contents.
     */
    private void updateProceedButtonState() {
        if (shoppingCart.getItems().isEmpty()) {
            proceedToCheckoutButton.setDisable(true);
        } else {
            proceedToCheckoutButton.setDisable(false);
        }
    }

    /**
     * Proceed to checkout.
     */
    public void proceedToCheckout() {
        if (shoppingCart.getItems().isEmpty()) {
            // Show an error dialog if the cart is empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Cart");
            alert.setHeaderText("Your cart is empty.");
            alert.setContentText("Please add items to your cart before proceeding to checkout.");
            alert.showAndWait();
            return;
        }

        double totalAmount = shoppingCart.getTotalPrice();

        // Use the customer's actual wallet balance
        Customer customer = (Customer) CurrentUser.getInstance().getUser();
        double walletBalance = customer.getBalance();

        if (walletBalance >= totalAmount) {
            // Navigate to the payment confirmation page
            navigateToPaymentPage(totalAmount, shoppingCart);
        } else {
            // Show an error dialog for insufficient balance
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Insufficient Balance");
            alert.setHeaderText("Insufficient Wallet Balance");
            alert.setContentText("Your wallet balance is insufficient to complete this purchase. Please recharge your wallet.");
            alert.showAndWait();
        }
    }

    private void navigateToPaymentPage(double totalAmount, ShoppingCart cart) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/storemanager/Checkout.fxml"));
            Parent paymentView = loader.load();

            // Pass data to the CheckoutController
            CheckoutController checkoutController = loader.getController();
            checkoutController.setCartDetails(cart);
            checkoutController.setTotalAmount(totalAmount);

            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.setScene(new Scene(paymentView));
            stage.setTitle("Checkout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
