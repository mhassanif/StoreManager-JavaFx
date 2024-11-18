package com.storemanager.model.items;

public class Category {

    private int id;       // Unique identifier for the category
    private String name;  // Category name

    // Constructor
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
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

    // ToString Method for debugging and printing
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
