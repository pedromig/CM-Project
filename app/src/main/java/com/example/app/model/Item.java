package com.example.app.model;

import java.util.ArrayList;
import java.util.Date;

public class Item {

    public static final int NEW_ITEM = -1;

    private String key;

    private String image;

    private String name;

    private String price;

    private Date expirationDate;

    private int quantity;

    private Home home;

    private ArrayList<Person> owners;

    public Item() {}

    public Item(Home home, String name, String price, int quantity, Date expirationDate) {
        this.name = name;
        this.home = home;
        this.price = price;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.owners = new ArrayList<>();
        this.owners.add(new Person(null, "Pedro", "924109520"));
    }

    public ArrayList<Person> getOwners() {
        return owners;
    }

    public void setOwners(ArrayList<Person> owners) {
        this.owners = owners;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
