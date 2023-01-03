package com.example.app.ui.home.models;

import java.util.Date;

public class Item {

    public static final int NEW_ITEM = -1;

    private String image;

    private String name;
    private String price;
    private Date expirationDate;
    private int quantity;

    public Item(String name, String price, String quantity, Date expirationDate) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
