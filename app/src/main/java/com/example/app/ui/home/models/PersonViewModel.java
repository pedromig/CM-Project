package com.example.app.ui.home.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app.model.Person;

import java.util.ArrayList;

public class PersonViewModel extends ViewModel {
    private final ArrayList<Person> people = new ArrayList<>();

    public ArrayList<Person> getPeople() {
        return this.people;
    }
    public void addPerson(Person person) {
        this.people.add(person);
    }
}