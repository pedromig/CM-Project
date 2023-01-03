package com.example.app.ui.home.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private final ArrayList<Home> residences = new ArrayList<>();

    public ArrayList<Home> getResidences() {
        return residences;
    }

    public void addResidence(Home home) {
        residences.add(home);
    }
}