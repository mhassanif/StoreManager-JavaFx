package com.storemanager.model.cart;

import com.storemanager.model.items.Product;
import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.users.Customer;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private List<CartItem> items;  // List to hold CartItem objects

    // Constructor to initialize an empty shopping cart
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    // Method to add a CartItem to the shopping cart
    public void addItem(CartItem item) {
        // Check if the item already exists in the cart (same product)
        for (CartItem cartItem : items) {
            if (cartItem.getProduct().getId() == item.getProduct().getId()) {
                // If the product is already in the cart, update the quantity
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        // If not in the cart, add it
        items.add(item);
    }

    // Method to remove a CartItem from the cart
    public void removeItem(CartItem item) {
        items.remove(item);
    }

    // Method to get the total price of all items in the cart
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    // Method to get the list of items in the cart
    public List<CartItem> getItems() {
        return items;
    }

    // Method to clear all items from the cart
    public void clearCart() {
        items.clear();
    }

    // Method to checkout and create an Order from the shopping cart
    public Order checkout(Customer customer) {
        List<OrderItem> orderItems = new ArrayList<>();

        // Convert CartItems to OrderItems
        for (CartItem cartItem : items) {
            // Convert each CartItem to OrderItem
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            OrderItem orderItem = new OrderItem(product, quantity);
            orderItems.add(orderItem);
        }

        // Create a new Order object for the customer with the order items
        Order order = new Order(customer, orderItems);

        // Clear the cart after placing the order
        clearCart();

        // Return the created order
        return order;
    }

    // ToString method to display the shopping cart's contents
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shopping Cart: \n");
        for (CartItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("Total Price: ").append(getTotalPrice());
        return sb.toString();
    }
}
