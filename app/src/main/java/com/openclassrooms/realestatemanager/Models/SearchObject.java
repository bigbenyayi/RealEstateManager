package com.openclassrooms.realestatemanager.Models;

public class SearchObject {
    String city;
    int roomsMin;
    int roomsMax;
    boolean sold;
    boolean available;
    String beginDate;
    String endDate;
    int photosMin;
    int photosMax;
    boolean park;
    boolean school;
    boolean restaurant;
    int surfaceMin;
    int surfaceMax;

    public SearchObject(String city, int roomsMin, int roomsMax, boolean sold, boolean available,
                        String beginDate, String endDate, int photosMin, int photosMax, boolean park,
                        boolean school, boolean restaurant, int surfaceMin, int surfaceMax) {

        this.city = city;
        this.roomsMin = roomsMin;
        this.roomsMax = roomsMax;
        this.sold = sold;
        this.available = available;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.photosMin = photosMin;
        this.photosMax = photosMax;
        this.park = park;
        this.school = school;
        this.restaurant = restaurant;
        this.surfaceMin = surfaceMin;
        this.surfaceMax = surfaceMax;
    }

    public String getCity() {
        return city;
    }

    public int getRoomsMin() {
        return roomsMin;
    }

    public int getRoomsMax() {
        return roomsMax;
    }

    public boolean isSold() {
        return sold;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getPhotosMin() {
        return photosMin;
    }

    public int getPhotosMax() {
        return photosMax;
    }

    public boolean isPark() {
        return park;
    }

    public boolean isSchool() {
        return school;
    }

    public boolean isRestaurant() {
        return restaurant;
    }

    public int getSurfaceMin() {
        return surfaceMin;
    }

    public int getSurfaceMax() {
        return surfaceMax;
    }
}
