package com.openclassrooms.realestatemanager.Models;

public class HorizontalRecyclerViewItem {

    String pictureUrl;
    String room;

    public HorizontalRecyclerViewItem(String pictureUrl, String room) {
        this.pictureUrl = pictureUrl;
        this.room = room;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getRoom() {
        return room;
    }
}
