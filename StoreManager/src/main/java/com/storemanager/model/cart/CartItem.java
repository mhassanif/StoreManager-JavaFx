package com.storemanager.model.cart;

import com.storemanager.model.items.Product;

public class CartItem {
    private Product product; // Composed Product
    private int quantity; // Quantity of the product in the cart

    // Constructor
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Calculate subtotal for the item in the cart
    public double calculateSubtotal() {
        return product.getPrice() * quantity;
    }
}
