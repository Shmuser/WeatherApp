package ru.tutudu.weatherapp;


import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class ClientWeather {
    private static ClientWeather ourInstance = new ClientWeather();
    private static Api api;
    private static String city;
    private static double lat, lon;
    static int readyThreads = 0;

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String BASE_URL_END = "&appid=WEATHERAPIKEY&units=metric";
    private static final String BASE_URL_END_DAY = "&appid=WEATHERAPIKEY&units=metric&cnt=8";


    public static ClientWeather getInstance() {
        return ourInstance;
    }

    private ClientWeather() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();


        api = retrofit.create(Api.class);
    }

    public Api getApi() {
        return api;
    }

    public void setLatLng(double lat1, double lon1) {
        lat = lat1;
        lon = lon1;
    }

    public interface Api {
        @GET("weather?" + BASE_URL_END)
        Observable<ResponseCurObj> getForecast(@Query(value = "lat", encoded = true) String lat,
                                               @Query(value="lon",encoded = true) String lon);

        @GET("forecast?" + BASE_URL_END_DAY)
        Observable<ResponseDayObj> getForecastDay(@Query(value = "lat", encoded = true) String lat,
                                               @Query(value="lon",encoded = true) String lon);

    }
}