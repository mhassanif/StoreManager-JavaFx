package com.storemanager.model.order;

import com.storemanager.model.items.Product;

public class OrderItem {
    private Product product; // Composed Product
    private int quantity; // Quantity of the product in the order
    private double priceAtPurchase; // Price of the product at the time of purchase

    // Constructor
    public OrderItem(Product product, int quantity, double priceAtPurchase) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase=product.getPrice();
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

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    // Calculate the total price for the order item
    public double calculateTotalPrice() {
        return priceAtPurchase * quantity;
    }
}
