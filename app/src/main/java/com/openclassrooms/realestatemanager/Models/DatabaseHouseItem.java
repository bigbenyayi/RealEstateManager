package com.openclassrooms.realestatemanager.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class DatabaseHouseItem {

    String description;
    String surface;
    @PrimaryKey @NonNull
    String id;
    String nbrOfRooms;
    String nbrOfBedrooms;
    String nbrOfBathrooms;
    String location;
    String realtor;
    String onMarket;
    String saleDate;
    String mainPicture;
    String price;
    String city;
    String type;

    @TypeConverters(GithubTypeConverters.class)
    List<String> pointsOfInterest;

    @TypeConverters(GithubTypeConverters.class)
    List<String> pictures;

    @TypeConverters(GithubTypeConverters.class)
    List<String> rooms;

    public DatabaseHouseItem(String description, String surface, @NonNull String id, String nbrOfRooms, String nbrOfBedrooms, String nbrOfBathrooms, String location, String realtor, String onMarket, String saleDate, String mainPicture, String price, String city, String type, List<String> pointsOfInterest, List<String> pictures, List<String> rooms) {
        this.description = description;
        this.surface = surface;
        this.id = id;
        this.nbrOfRooms = nbrOfRooms;
        this.nbrOfBedrooms = nbrOfBedrooms;
        this.nbrOfBathrooms = nbrOfBathrooms;
        this.location = location;
        this.realtor = realtor;
        this.onMarket = onMarket;
        this.saleDate = saleDate;
        this.mainPicture = mainPicture;
        this.price = price;
        this.city = city;
        this.type = type;
        this.pointsOfInterest = pointsOfInterest;
        this.pictures = pictures;
        this.rooms = rooms;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public List<String> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNbrOfRooms() {
        return nbrOfRooms;
    }

    public void setNbrOfRooms(String nbrOfRooms) {
        this.nbrOfRooms = nbrOfRooms;
    }

    public String getNbrOfBedrooms() {
        return nbrOfBedrooms;
    }

    public void setNbrOfBedrooms(String nbrOfBedrooms) {
        this.nbrOfBedrooms = nbrOfBedrooms;
    }

    public String getNbrOfBathrooms() {
        return nbrOfBathrooms;
    }

    public void setNbrOfBathrooms(String nbrOfBathrooms) {
        this.nbrOfBathrooms = nbrOfBathrooms;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRealtor() {
        return realtor;
    }

    public void setRealtor(String realtor) {
        this.realtor = realtor;
    }

    public String getOnMarket() {
        return onMarket;
    }

    public void setOnMarket(String onMarket) {
        this.onMarket = onMarket;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }


    public void setRooms(ArrayList<String> rooms) {
        this.rooms = rooms;
    }

    public String getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(String mainPicture) {
        this.mainPicture = mainPicture;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

//    public static DatabaseHouseItem fromContentValues(ContentValues values) {
//        final DatabaseHouseItem item = new DatabaseHouseItem();
//        if (values.containsKey("text")) item.setText(values.getAsString("text"));
//        if (values.containsKey("category")) item.setCategory(values.getAsInteger("category"));
//        if (values.containsKey("isSelected")) item.setSelected(values.getAsBoolean("isSelected"));
//        if (values.containsKey("userId")) item.setUserId(values.getAsLong("userId"));
//        if (values.containsKey("image")) item.setImage(values.getAsString("image"));
//        return item;
//    }
}

