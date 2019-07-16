package ru.tutudu.weatherapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Location.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract LocationDao locationDao();
}
