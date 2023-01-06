package com.example.app.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Person {

    private String key;

    private String name;

    private String email;

    private ArrayList<String> shoppingList;

    private HashMap<String, Double> debts;

    public Person() {}

    public Person(String email) {
        this.email = email;
        this.name = "Unkown";
        this.shoppingList = new ArrayList<>();
        this.debts = new HashMap<>();
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

    public ArrayList<String> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ArrayList<String> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void setDebts(HashMap<String, Double> debts) {
        this.debts = debts;
    }

    public HashMap<String, Double> getDebts() {
        return debts;
    }
}