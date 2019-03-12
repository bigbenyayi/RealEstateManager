package com.openclassrooms.realestatemanager.Models;

import java.util.ArrayList;

public class RecyclerViewItem {

    String mainPicture;
    String id;
    String type;
    String city;
    String price;
    String numberOfRooms;
    String onMarket;
    String sold;
    ArrayList<String> pictures;
    ArrayList<String> pointOfInterest;
    String surface;

    public RecyclerViewItem(){}

    public RecyclerViewItem(String mainPicture, String id, String type, String city,
                            String price, String numberOfRooms, String onMarket, String sold,
                            ArrayList<String> pictures, ArrayList<String> pointOfInterest, String surface) {
        this.mainPicture = mainPicture;
        this.id = id;
        this.type = type;
        this.city = city;
        this.price = price;
        this.numberOfRooms = numberOfRooms;
        this.onMarket = onMarket;
        this.sold = sold;
        this.pictures = pictures;
        this.pointOfInterest = pointOfInterest;
        this.surface = surface;
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
}
