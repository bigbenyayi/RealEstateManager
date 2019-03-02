package com.openclassrooms.realestatemanager.Models;

public class User {

    private String mUid;
    private String mUserName;

    public User(String uid, String userName) {
        mUid = uid;
        mUserName = userName;
    }

    public String getUid() {
        return mUid;
    }

    public String getUserName() {
        return mUserName;
    }
}
