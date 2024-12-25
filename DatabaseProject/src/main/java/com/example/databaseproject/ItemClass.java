package com.example.databaseproject;

public class ItemClass{
    private String name;
    private String description;
    private double price;
    private String location;
    private String category;

    public ItemClass(String name, String description, double price, String location, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}