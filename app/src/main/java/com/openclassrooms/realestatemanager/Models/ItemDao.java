package com.openclassrooms.realestatemanager.Models;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;
import android.content.Context;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM DatabaseHouseItem")
    List<DatabaseHouseItem> getItems();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insertItem(DatabaseHouseItem item);

    @Update
    int updateItem(DatabaseHouseItem item);

    @Query("DELETE FROM DatabaseHouseItem WHERE id = :itemId")
    int deleteItem(long itemId);
}

