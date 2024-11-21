package com.storemanager.model.items;

import com.storemanager.model.cart.CartItem;

public class Product {

    private int id;              // Unique identifier for the product
    private String name;         // Name of the product
    private double price;        // Price of the product
    private String brand;        // Brand of the product
    private String imageUrl;     // URL for the product image
    private Category category;   // Category to which the product belongs
    private String description;  // Nullable description of the product

    // Constructor
    public Product(int id, String name, double price, String brand, String imageUrl, Category category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description; // Description is optional
    }

    // Overloaded constructor without description (for backward compatibility)
    public Product(int id, String name, double price, String brand, String imageUrl, Category category) {
        this(id, name, price, brand, imageUrl, category, null); // Default description to null
    }

    // Getters and Setters


    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ToString Method for debugging and printing
    @Override
    public String toString() {
        return "Product{id=" + id +
                ", name='" + name +
                "', price=" + price +
                ", brand='" + brand +
                "', imageUrl='" + imageUrl +
                "', category=" + category.getName() +
                ", description='" + (description != null ? description : "No description") + "'}";
    }

    public int getId() {
        return id;
    }
}
