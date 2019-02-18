package com.openclassrooms.realestatemanager.Models;

public class RecyclerViewItem {

    String picture;
    String type;
    String location;
    String price;

    public RecyclerViewItem(String picture, String type, String location, String price) {
        this.picture = picture;
        this.type = type;
        this.location = location;
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
