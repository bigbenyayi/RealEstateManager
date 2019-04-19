package com.openclassrooms.realestatemanager.Models;

import java.util.List;

@SuppressWarnings("ALL")
public class DetailHouse {
    String description;
    @SuppressWarnings("WeakerAccess")
    String surface;
    String nbrOfRooms;
    String nbrOfBedrooms;
    String nbrOfBathrooms;
    String address;
    String realtor;
    String onMarket;
    String saleDate;
    List<String> pointsOfInterest;
    List<String> pictures;

    public DetailHouse(String description, String surface, String nbrOfRooms, String nbrOfBedrooms, String nbrOfBathrooms, String address, String realtor, String onMarket, String saleDate, List<String> pointsOfInterest, List<String> pictures) {
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

    public List<String> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public List<String> getPictures() {
        return pictures;
    }
}
