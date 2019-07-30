package ru.tutudu.weatherapp;

import android.app.Activity;
import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Locale;

import io.reactivex.Observable;


public class App extends Application{
    public static App instance;
    private AppDatabase database;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .allowMainThreadQueries().build();
    }

    public static void setAppLocale(String language, Activity activity) {
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(language));
        activity.getApplicationContext().createConfigurationContext(configuration);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.onAttach(base, "en"));
    }


    public static App getInstance() {
        return instance;
    }


    public AppDatabase getDatabase() {
        return database;
    }


    public static Observable<Boolean> isInternetOn(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return Observable.just(activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }
}
