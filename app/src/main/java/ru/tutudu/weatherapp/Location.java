package ru.tutudu.weatherapp;

import android.arch.persistence.room.Entity;


@Entity(primaryKeys = {"lon", "lat"})
public class Location {
    double lon;
    double lat;
    public String name;
    public boolean isFavorite;
}
