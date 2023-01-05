package com.example.app.model;

public class Person {
    private String picture;
    private String name;
    private String phoneNumber;

    public Person() {}

    public Person(String picture, String name, String phoneNumber) {
        this.picture = picture;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
