package com.openclassrooms.realestatemanager.Models;

import java.util.ArrayList;

public class RecyclerViewItem {

    String mainPicture;
    String id;
    String type;
    String city;
    String price;
    String numberOfRooms;
    String numberOfBedrooms;
    String numberOfBathrooms;
    String onMarket;
    String sold;
    String description;
    ArrayList<String> pictures;
    ArrayList<String> rooms;
    ArrayList<String> pointOfInterest;
    String surface;
    String location;
    String realtor;

    public RecyclerViewItem(){}

    public RecyclerViewItem(String mainPicture, String id, String type, String city, String price, String numberOfRooms, String numberOfBedrooms, String numberOfBathrooms, String onMarket, String sold, String description, ArrayList<String> pictures, ArrayList<String> rooms, ArrayList<String> pointOfInterest, String surface, String location, String realtor) {
        this.mainPicture = mainPicture;
        this.id = id;
        this.type = type;
        this.city = city;
        this.price = price;
        this.numberOfRooms = numberOfRooms;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.onMarket = onMarket;
        this.sold = sold;
        this.description = description;
        this.pictures = pictures;
        this.rooms = rooms;
        this.pointOfInterest = pointOfInterest;
        this.surface = surface;
        this.location = location;
        this.realtor = realtor;
    }

    public ArrayList<String> getRooms() {
        return rooms;
    }

    public String getNumberOfRooms() {
        return numberOfRooms;
    }

    public String getOnMarket() {
        return onMarket;
    }

    public String getSold() {
        return sold;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public ArrayList<String> getPointOfInterest() {
        return pointOfInterest;
    }

    public String getSurface() {
        return surface;
    }

    public String getMainPicture() {
        return mainPicture;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCity() {
        return city;
    }

    public String getPrice() {
        return price;
    }

    public String getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public String getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getRealtor() {
        return realtor;
    }
}
