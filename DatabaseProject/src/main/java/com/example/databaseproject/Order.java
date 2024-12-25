package com.example.databaseproject;


public class Order {
    private int orderId;
    private int userId;
    private int itemId;
    private int quantity;
    private String status;

    // Items tablosundan alınan ek bilgiler
    private String itemName;
    private double itemPrice;

    public Order(int orderId, int userId, int itemId, int quantity, String status, String itemName, double itemPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.status = status;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    // Getter ve Setter metodları
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }
}