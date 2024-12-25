package com.example.databaseproject;

public class FavoriteItem {
    private int favoriteId;
    private int itemId;
    private String name;
    private String description;
    private double price;
    private String location;

    public FavoriteItem(int favoriteId, int itemId, String name, String description, double price, String location) {
        this.favoriteId = favoriteId;
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}