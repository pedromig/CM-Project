package com.example.app.ui.home.models;

public class Home {

    public static final int NEW_HOME = -1;

    private String name;

    public Home(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
