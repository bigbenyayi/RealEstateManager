package com.openclassrooms.realestatemanager.Models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String userName) {
        com.openclassrooms.realestatemanager.Models.User userToCreate = new  com.openclassrooms.realestatemanager.Models.User(uid, userName);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }
}
