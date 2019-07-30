package ru.tutudu.weatherapp;

import android.arch.persistence.room.Entity;


@Entity(primaryKeys = {"lon", "lat"})
public class Location {
    double lon;
    double lat;
    String city, country;
    public String name;
    public boolean isFavorite;
}
