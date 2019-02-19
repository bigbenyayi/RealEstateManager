package com.openclassrooms.realestatemanager.Models;

import org.json.JSONArray;

import java.util.ArrayList;

public class DetailHouse {
    String description;
    String surface;
    String nbrOfRooms;
    String nbrOfBedrooms;
    String nbrOfBathrooms;
    String address;
    JSONArray pictures;

    public DetailHouse(String description, String surface, String nbrOfRooms, String nbrOfBedrooms, String nbrOfBathrooms, String address, JSONArray pictures) {
        this.description = description;
        this.surface = surface;
        this.nbrOfRooms = nbrOfRooms;
        this.nbrOfBedrooms = nbrOfBedrooms;
        this.nbrOfBathrooms = nbrOfBathrooms;
        this.address = address;
        this.pictures = pictures;
    }

    public String getDescription() {
        return description;
    }

    public String getSurface() {
        return surface;
    }

    public String getNbrOfRooms() {
        return nbrOfRooms;
    }

    public String getNbrOfBedrooms() {
        return nbrOfBedrooms;
    }

    public String getNbrOfBathrooms() {
        return nbrOfBathrooms;
    }

    public String getAddress() {
        return address;
    }

    public JSONArray getPictures() {
        return pictures;
    }
}
