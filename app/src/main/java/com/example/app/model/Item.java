package com.example.app.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Item {
    public static final int NEW_ITEM = -1;

    private String key;

    private String image;

    private String name;

    private double price;

    private String expirationDate;

    private int quantity;

    private String homeKey;

    private ArrayList<String> owners;

    public Item() {}

    public Item(String homeKey, String name, double price, int quantity,
                String expirationDate, ArrayList<String> owners) {
        this.name = name;
        this.homeKey = homeKey;
        this.price = price;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.owners = owners;
    }

    public String getHomeKey() {
        return homeKey;
    }

    public void setHomeKey(String homeKey) {
        this.homeKey = homeKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getPrice() {
        return price;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<String> getOwners() {
        return owners;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }
}
