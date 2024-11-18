package com.storemanager.model.items;

public class InventoryProduct {
    private Product product;
    private int stockLevel;
    private int restockLevel;
    private String restockDate;

    // Constructor
    public InventoryProduct(Product product, int stockLevel, int restockLevel, String restockDate) {
        this.product = product;
        this.stockLevel = stockLevel;
        this.restockLevel = restockLevel;
        this.restockDate = restockDate;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public String getRestockDate() {
        return restockDate;
    }

    public void setRestockDate(String restockDate) {
        this.restockDate = restockDate;
    }

    // Utility methods
    public boolean needsRestock() {
        return stockLevel <= restockLevel;
    }

    @Override
    public String toString() {
        return "InventoryProduct{" +
                "product=" + product +
                ", stockLevel=" + stockLevel +
                ", restockLevel=" + restockLevel +
                ", restockDate='" + restockDate + '\'' +
                '}';
    }
}
