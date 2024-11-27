package com.storemanager.controlers;

import com.storemanager.auth.CurrentUser;
import com.storemanager.dao.ShoppingCartDAO;
import com.storemanager.db.DBconnector;
import com.storemanager.model.cart.CartItem;
import com.storemanager.model.cart.ShoppingCart;
import com.storemanager.model.items.Product;
import com.storemanager.model.users.Customer; // Assuming you have a Customer model
import com.storemanager.model.users.User; // Assuming you have a User model
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private int currentUserId; // Holds the logged-in user ID

    @FXML
    public void initialize() {
        // Assuming we have a session manager or UserSession to get the current logged-in user
        currentUserId = ((Customer)CurrentUser.getInstance().getUser()).getCustomerId(); // Fetch the logged-in user's ID

        // Initialize ShoppingCart for the logged-in customer
        //shoppingCart = ShoppingCartDAO.getShoppingCartByCustomerId(currentUserId); // Dynamically load the cart for the user
        shoppingCart=((Customer)CurrentUser.getInstance().getUser()).getShoppingCart();

        // Set up table columns
        colProductName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        colProductBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getBrand()));
        //colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().calculateSubtotal()).asObject());

        // Load items into the table
        loadCartItems();

        // Add action buttons for quantity adjustment
        addActionButtons();
    }


    /**
     * Load cart items from the database into the TableView.
     */
    private void loadCartItems() {
        shoppingCart.getItems().clear();

/*        try (Connection connection = DBconnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT ci.quantity, ci.price, p.product_id, p.name, p.brand, p.description, p.url " +
                             "FROM CARTITEM ci " +
                             "JOIN PRODUCT p ON ci.product_id = p.product_id " +
                             "WHERE ci.cart_id = ?")) {

            ps.setInt(1, shoppingCart.getCartId());
            ResultSet rs = ps.executeQuery();

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
                CartItem cartItem = new CartItem(product, rs.getInt("quantity"));
                shoppingCart.addItem(cartItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        shoppingCart.loadItems();

        ObservableList<CartItem> cartItems = FXCollections.observableArrayList(shoppingCart.getItems());
        cartTable.setItems(cartItems);
        updateTotalAmount();
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
                        ShoppingCartDAO.removeItem(shoppingCart.getCartId(),item.getProduct().getId());
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
        try (Connection connection = DBconnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE CARTITEM SET quantity = ?, price = ? WHERE cart_id = ? AND product_id = ?")) {

            ps.setInt(1, item.getQuantity());
            ps.setDouble(2, totalPrice); // Update the total price in the database
            ps.setInt(3, shoppingCart.getCartId());
            ps.setInt(4, item.getProduct().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
     * Proceed to checkout (placeholder for checkout functionality).
     */
    public void proceedToCheckout() {
        double totalAmount = shoppingCart.getTotalPrice();
        double walletBalance = getHardcodedWalletBalance(); // Use the hardcoded wallet balance

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

    /**
     * Returns a hardcoded wallet balance for testing purposes.
     */
    private double getHardcodedWalletBalance() {
        return 5000.00; // Hardcoded wallet balance
    }

}