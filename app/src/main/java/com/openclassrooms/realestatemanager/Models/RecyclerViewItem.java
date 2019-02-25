package com.openclassrooms.realestatemanager.Models;

public class RecyclerViewItem {

    String mainPicture;
    String id;
    String type;
    String city;
    String price;

    public RecyclerViewItem(){}

    public RecyclerViewItem(String id, String picture, String type, String location, String price) {
        this.mainPicture = picture;
        this.type = type;
        this.city = location;
        this.price = price;
        this.id = id;
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
