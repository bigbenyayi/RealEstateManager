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
    int priceMin;
    int priceMax;

    public SearchObject(String city, int roomsMin, int roomsMax, boolean sold, boolean available, String beginDate, String endDate, int photosMin, int photosMax, boolean park, boolean school, boolean restaurant, int surfaceMin, int surfaceMax, int priceMin, int priceMax) {
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
        this.priceMin = priceMin;
        this.priceMax = priceMax;
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

    public int getPriceMin() {
        return priceMin;
    }

    public int getPriceMax() {
        return priceMax;
    }

    public void setPriceMin(int priceMin) {
        this.priceMin = priceMin;
    }

    public void setPriceMax(int priceMax) {
        this.priceMax = priceMax;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRoomsMin(int roomsMin) {
        this.roomsMin = roomsMin;
    }

    public void setRoomsMax(int roomsMax) {
        this.roomsMax = roomsMax;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setPhotosMin(int photosMin) {
        this.photosMin = photosMin;
    }

    public void setPhotosMax(int photosMax) {
        this.photosMax = photosMax;
    }

    public void setPark(boolean park) {
        this.park = park;
    }

    public void setSchool(boolean school) {
        this.school = school;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    public void setSurfaceMin(int surfaceMin) {
        this.surfaceMin = surfaceMin;
    }

    public void setSurfaceMax(int surfaceMax) {
        this.surfaceMax = surfaceMax;
    }
}
