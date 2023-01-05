package com.example.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Home {

    private String key;

    private String name;

    private String picture;

    private String location;

    private ArrayList<Person> members;

    public Home() {}

    public Home(String name) {
        this.picture = null;
        this.name = name;
        this.members = new ArrayList<>();
        this.location = "";
        this.members.add(new Person(null, "Pedro", "924109520"));
    }

    public Home(String name, String location) {
        this(name);
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<Person> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Person> members) {
        this.members = members;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
