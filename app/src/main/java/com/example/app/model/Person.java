package com.example.app.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Person {

    private String picture;
    private String key;

    private String name;

    private String email;

    private ArrayList<String> shopping;

    private HashMap<String, Double> debts;

    public Person() {}

    public Person(String email) {
        this.email = email;
        this.name = "Unkown";
        this.shopping= new ArrayList<>();
        this.debts = new HashMap<>();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getShopping() {
        return shopping;
    }

    public void setShopping(ArrayList<String> shoppingList) {
        this.shopping= shoppingList;
    }

    public void setDebts(HashMap<String, Double> debts) {
        this.debts = debts;
    }

    public HashMap<String, Double> getDebts() {
        return debts;
    }

    @Override
    public String toString() {
        return "Person{" +
                "picture='" + picture + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", shopping=" + shopping +
                ", debts=" + debts +
                '}';
    }
}