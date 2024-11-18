package com.storemanager.model.items;

public class Product {

    private int id;              // Unique identifier for the product
    private String name;         // Name of the product
    private double price;        // Price of the product
    private int stockLevel;      // Available quantity of the product in stock
    private int restockLevel;    // Minimum level for restocking
    private String brand;        // Brand of the product
    private String imageUrl;     // URL for the product image
    private Category category;   // Category to which the product belongs

    // Constructor
    public Product(int id, String name, double price, int stockLevel, int restockLevel,
                   String brand, String imageUrl, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockLevel = stockLevel;
        this.restockLevel = restockLevel;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

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

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public int getRestockLevel() {
        return restockLevel;
    }

    public void setRestockLevel(int restockLevel) {
        this.restockLevel = restockLevel;
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

    // ToString Method for debugging and printing
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + ", stockLevel=" + stockLevel +
                ", restockLevel=" + restockLevel + ", brand='" + brand + "', imageUrl='" + imageUrl +
                "', category=" + category.getName() + "}";
    }
}
