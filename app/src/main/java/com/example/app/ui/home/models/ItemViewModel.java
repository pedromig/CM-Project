package com.example.app.ui.home.models;

import androidx.lifecycle.ViewModel;

import com.example.app.model.Item;
import com.example.app.model.Person;

import java.util.ArrayList;

public class ItemViewModel extends ViewModel {
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Person> owners = new ArrayList<>();

    private boolean set = false;
    private double savedAmount;
    private ArrayList<Person> saved = new ArrayList<>();

    public void setSet(boolean set) {
        this.set = set;
    }

    public boolean isSet() {
        return set;
    }
    public void setSaved(ArrayList<Person> saved) {
        this.saved = saved;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public ArrayList<Person> getOwners() {
        return owners;
    }
    public void addOwner(Person owner) {
        this.owners.add(owner);
    }

    public ArrayList<Person> getSaved() {
        return saved;
    }
}
