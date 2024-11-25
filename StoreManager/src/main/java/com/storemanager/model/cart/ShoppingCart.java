package com.storemanager.model.cart;

import com.storemanager.dao.ShoppingCartDAO;
import com.storemanager.model.items.Product;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.users.Customer;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private int cartId;           // Unique identifier for the shopping cart from the database
    private List<CartItem> items; // List to hold CartItem objects

    // Constructor to initialize a shopping cart with a cartId and an empty list of items
    public ShoppingCart(int cartId) {
        this.cartId = cartId;
        this.items = ShoppingCartDAO.getItemsByCartId(cartId);
    }

    // Getters and Setters for cartId
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    // Method to add or update a CartItem in the shopping cart and synchronize with the database
    public void addItem(CartItem item) {
        boolean itemExists = false;

        // Check if the item already exists in the cart (same product)
        for (CartItem cartItem : items) {
            if (cartItem.getProduct().getId() == item.getProduct().getId()) {
                // If the product is already in the cart, update the quantity
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            // If not in the cart, add it to the list
            items.add(item);
        }

        // Use DAO method to add or update the item in the database
        ShoppingCartDAO.addOrUpdateItem(cartId, item);
    }

    // Method to remove a CartItem from the cart and update the database
    public void removeItem(CartItem item) {
        // Remove from the list
        items.removeIf(cartItem -> cartItem.getProduct().getId() == item.getProduct().getId());
        // Remove from the database
        ShoppingCartDAO.removeItem(cartId, item.getProduct().getId());
    }

    // Method to get the total price of all items in the cart
    public double getTotalPrice() {
        return items.stream().mapToDouble(CartItem::calculateSubtotal).sum();
    }

    // Method to get the list of items in the cart
    public List<CartItem> getItems() {
        return items;
    }

    // Method to clear all items from the cart and the database
    public void clearCart() {
        items.clear();
        ShoppingCartDAO.clearCart(cartId);
    }

    // Method to checkout and create an Order from the shopping cart
    public Order checkout(Customer customer) {
        List<OrderItem> orderItems = new ArrayList<>();

        // Convert CartItems to OrderItems
        for (CartItem cartItem : items) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            OrderItem orderItem = new OrderItem(product, quantity);
            orderItems.add(orderItem);
        }

        // Create a new Order object for the customer with the order items
        Order order = new Order(customer, orderItems);

        // Clear the cart after placing the order
        clearCart();

        return order;
    }

    // ToString method to display the shopping cart's contents
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shopping Cart (ID: ").append(cartId).append("):\n");
        for (CartItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("Total Price: ").append(getTotalPrice());
        return sb.toString();
    }
}
