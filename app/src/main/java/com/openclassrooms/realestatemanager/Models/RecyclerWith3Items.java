package com.openclassrooms.realestatemanager.Models;

public class RecyclerWith3Items {

    String id;
    String type;
    String city;
    String price;
    String picture;

    public RecyclerWith3Items(String id, String type, String city, String price, String picture) {
        this.id = id;
        this.type = type;
        this.city = city;
        this.price = price;
        this.picture = picture;
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

    public String getCity() {
        return city;
    }

    public String getPrice() {
        return price;
    }
}
