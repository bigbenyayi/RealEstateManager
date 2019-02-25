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
    String realtor;
    String onMarket;
    String saleDate;
    ArrayList<String> pointsOfInterest;
    ArrayList<String> pictures;

    public DetailHouse(String description, String surface, String nbrOfRooms, String nbrOfBedrooms, String nbrOfBathrooms, String address, String realtor, String onMarket, String saleDate, ArrayList<String> pointsOfInterest, ArrayList<String> pictures) {
        this.description = description;
        this.surface = surface;
        this.nbrOfRooms = nbrOfRooms;
        this.nbrOfBedrooms = nbrOfBedrooms;
        this.nbrOfBathrooms = nbrOfBathrooms;
        this.address = address;
        this.realtor = realtor;
        this.onMarket = onMarket;
        this.saleDate = saleDate;
        this.pointsOfInterest = pointsOfInterest;
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


    public String getRealtor() {
        return realtor;
    }

    public String getOnMarket() {
        return onMarket;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public ArrayList<String> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }
}
