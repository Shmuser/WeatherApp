package ru.tutudu.weatherapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface LocationDao {


    @Query("select * from location")
    List<Location> getAll();


    @Query("select * from location where isFavorite = 1")
    List<Location> getAllFavorites();


    @Insert
    void insert(Location location);


    @Delete
    void delete(Location location);


    @Update
    void update(Location location);
}
