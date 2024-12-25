package com.example.databaseproject;

public class Item {
    private int itemId;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private int userId;
    private String location;

    public Item(int itemId, String name, String description, double price, int categoryId, int userId, String location) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.userId = userId;
        this.location = location;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}