package com.example.app.ui.home.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app.model.Home;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private ArrayList<Home> residences = new ArrayList<>();

    public ArrayList<Home> getResidences() {
        return residences;
    }

    public void setResidences(ArrayList<Home> residences) {
        this.residences = residences;
    }

    public void addResidence(Home home) {
        residences.add(home);
    }
}