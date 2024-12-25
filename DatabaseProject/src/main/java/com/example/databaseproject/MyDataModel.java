package com.example.databaseproject;

public class MyDataModel {
    private final int id;
    private final String kategori;

    public MyDataModel(int id, String kategori) {
        this.id = id;
        this.kategori = kategori;
    }

    public int getId() {
        return id;
    }

    public String getKategori() {
        return kategori;
    }
}
