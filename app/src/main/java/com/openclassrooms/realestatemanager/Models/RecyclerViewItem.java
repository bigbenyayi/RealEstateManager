package com.openclassrooms.realestatemanager.Models;

public class RecyclerViewItem {

    String picture;
    String id;
    String type;
    String location;
    String price;

    public RecyclerViewItem(String id, String picture, String type, String location, String price) {
        this.picture = picture;
        this.type = type;
        this.location = location;
        this.price = price;
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }
}
