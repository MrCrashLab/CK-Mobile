package com.example.ckproject.model;

public class Parking {
    private int id;
    private int id_point;
    private String name;
    private String description;
    private String address;
    private int all_slot;
    private int free_slot;

    public int getId() {
        return id;
    }

    public int getId_point() {
        return id_point;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getAll_slot() {
        return all_slot;
    }

    public int getFree_slot() {
        return free_slot;
    }
}
