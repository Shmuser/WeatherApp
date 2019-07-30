package ru.tutudu.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class WeatherActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Location curLoc = new Location();
    SwipeRefreshLayout refreshLayout;
    private ImageView curWeatherImage;
    private TextView curTemp;
    private ArrayList<DayTimeWeather> dayTimeWeathers = new ArrayList<>();
    private TextView curWind, curPressure, curHumidity;
    private ArrayList<String> daytimes = new ArrayList<>();
    private ClientWeather.Api apiCur = ClientWeather.getInstance().getApi();
    io.reactivex.subjects.Subject<Boolean> needToAlert = PublishSubject.create();

    class DayTimeWeather {
        TextView dayTimeName;
        ImageView weatherImage;
        TextView temp;
    }


    private void updateUI() {
        refreshLayout.setRefreshing(true);
        int readyThreads = 0;
        apiCur.getForecast(String.valueOf(curLoc.lat), String.valueOf(curLoc.lon)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseCurObj>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ResponseCurObj responseCurObj) {
                        updateUIWithCur(responseCurObj);
                        ClientWeather.readyThreads++;
                        if (ClientWeather.readyThreads == 2) {
                            refreshLayout.setRefreshing(false);
                            ClientWeather.readyThreads = 0;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {}
                });
        apiCur.getForecastDay(String.valueOf(curLoc.lat), String.valueOf(curLoc.lon)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseDayObj>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseDayObj responseDayObj) {
                        updateUIWithDay(responseDayObj);
                        ClientWeather.readyThreads++;
                        if (ClientWeather.readyThreads == 2) {
                            refreshLayout.setRefreshing(false);
                            ClientWeather.readyThreads = 0;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        curLoc.name = intent.getStringExtra("name");
        curLoc.isFavorite = intent.getBooleanExtra("isFav", false);
        curLoc.city = intent.getStringExtra("city");
        curLoc.country = intent.getStringExtra("country");
        curLoc.lat = getIntent().getDoubleExtra("lat", 0);
        curLoc.lon = getIntent().getDoubleExtra("lon", 0);
        setContentView(R.layout.activity_weather);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar_weather);
        toolbar.setTitle(curLoc.name);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        for (int i = 0; i < 4; ++i)
            dayTimeWeathers.add(new DayTimeWeather());
        daytimes.add(getString(R.string.morning));
        daytimes.add(getString(R.string.day));
        daytimes.add(getString(R.string.evening));
        daytimes.add(getString(R.string.night));

        View view = findViewById(R.id.weather_info);
        TextView place = view.findViewById(R.id.w_loc_place);

        if (curLoc.country != null) {
            if (curLoc.city != null) {
                place.setText(curLoc.city + ",\n" + curLoc.country);
            }
            else {
                place.setText(curLoc.country);
            }
        }
        TextView date = view.findViewById(R.id.w_date);
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        date.setText(currentDate);

        curWeatherImage = view.findViewById(R.id.w_weather_image);
        curTemp = view.findViewById(R.id.w_cur_temp);

        view = findViewById(R.id.wd_layout);
        View view1 = view.findViewById(R.id.wd_1);
        dayTimeWeathers.get(0).dayTimeName = view1.findViewById(R.id.wd_daytime);
        dayTimeWeathers.get(0).weatherImage = view1.findViewById(R.id.wd_image);
        dayTimeWeathers.get(0).temp = view1.findViewById(R.id.wd_temp);

        view1 = view.findViewById(R.id.wd_2);
        dayTimeWeathers.get(1).dayTimeName = view1.findViewById(R.id.wd_daytime);
        dayTimeWeathers.get(1).weatherImage = view1.findViewById(R.id.wd_image);
        dayTimeWeathers.get(1).temp = view1.findViewById(R.id.wd_temp);

        view1 = view.findViewById(R.id.wd_3);
        dayTimeWeathers.get(2).dayTimeName = view1.findViewById(R.id.wd_daytime);
        dayTimeWeathers.get(2).weatherImage = view1.findViewById(R.id.wd_image);
        dayTimeWeathers.get(2).temp = view1.findViewById(R.id.wd_temp);

        view1 = view.findViewById(R.id.wd_4);
        dayTimeWeathers.get(3).dayTimeName = view1.findViewById(R.id.wd_daytime);
        dayTimeWeathers.get(3).weatherImage = view1.findViewById(R.id.wd_image);
        dayTimeWeathers.get(3).temp = view1.findViewById(R.id.wd_temp);

        view = findViewById(R.id.cf_layout);
        view1 = view.findViewById(R.id.cf_wind);
        ((ImageView)view1.findViewById(R.id.cf_image)).setImageResource(R.drawable.weather_windy);
        curWind = view1.findViewById(R.id.cf_text);
        view1 = view.findViewById(R.id.cf_pressure);
        ((ImageView)view1.findViewById(R.id.cf_image)).setImageResource(R.drawable.chevron_triple_down);
        curPressure = view1.findViewById(R.id.cf_text);
        view1 = view.findViewById(R.id.cf_humidity);
        ((ImageView)view1.findViewById(R.id.cf_image)).setImageResource(R.drawable.water_percent);
        curHumidity = view1.findViewById(R.id.cf_text);


        needToAlert.subscribe(new Observer<Boolean>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean)
                    Toast.makeText(getApplicationContext(), getString(R.string.internet_info), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        ReactiveNetwork
                .observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connected -> {
                    if (!connected) {
                        needToAlert.onNext(!connected);
                    }
                });


        updateUI();
    }


    @Override
    public void onRefresh() {
        updateUI();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }


    void updateUIWithCur(ResponseCurObj obj) {
        curTemp.setText(formatTemp(obj.getMain().getTemp()));
        String imageName = "p" + obj.getWeather().get(0).getIcon();
        int imageId = getResId(String.valueOf(imageName), R.drawable.class);
        curWeatherImage.setImageResource(imageId);
        curHumidity.setText(getString(R.string.humidityTitle) + ": "
                + obj.getMain().getHumidity() + "%");
        double convertCoef = 0.75006157584566;
        double pressure = Double.valueOf(obj.getMain().getPressure()) * convertCoef;
        curPressure.setText(getString(R.string.pressureTitle) + ": "
                + String.valueOf(new DecimalFormat("###")
                .format(pressure)) + " " + getString(R.string.pressureM));
        String windSpeed = obj.getWind().getSpeed();
        Double windDeg = Double.valueOf(obj.getWind().getDeg());
        curWind.setText(getString(R.string.windTitle) + ": " + getWindDir(windDeg)
                + ", " + windSpeed + " " + getString(R.string.windSpeed));
    }

    private void updateUIWithDay(ResponseDayObj obj) {
        for (int i = 0; i < 8; ++i) {
            String title = getDayTime(obj.getList().get(i).getDtTxt());
            if (title != null) {
                dayTimeWeathers.get(i / 2).dayTimeName.setText(title);
                String imageName = "p" + obj.getList().get(i).getWeather().get(0).getIcon();
                int imageId = getResId(String.valueOf(imageName), R.drawable.class);
                dayTimeWeathers.get(i / 2).weatherImage.setImageResource(imageId);
                String temp = obj.getList().get(i).getMain().getTemp();
                dayTimeWeathers.get(i / 2).temp.setText(formatTemp(temp));
            }
        }
    }


    private String formatTemp(String value) {
        String temp = String.valueOf(new DecimalFormat("##.#")
                .format(Double.valueOf(value)));
        temp = temp.replace(",", ".");
        if (Double.valueOf(temp) > 0) {
            temp = "+" + temp;
        }
        else {
            temp = "-" + temp;
        }
        temp += "\u2103";
        return temp;
    }


    private String getDayTime(String time) {
        String date = time.split(" ")[0].replace("-", ".");
        String hour = time.split(" ")[1].split(":")[0];
        if (hour.equals("00")) return daytimes.get(3) + "\n" + date;
        if (hour.equals("06")) return daytimes.get(0) + "\n" + date;
        if (hour.equals("12")) return daytimes.get(1) + "\n" + date;
        if (hour.equals("18")) return daytimes.get(2) + "\n" + date;
        return null;
    }


    private String getWindDir(double degree) {
        if (degree > 337.5) return getString(R.string.windN);
        if (degree > 292.5) return getString(R.string.windNW);
        if (degree > 247.5) return getString(R.string.windW);
        if (degree > 202.5) return getString(R.string.windSW);
        if (degree > 157.5) return getString(R.string.windS);
        if (degree > 122.5) return getString(R.string.windSE);
        if (degree > 67.5) return getString(R.string.windE);
        if (degree > 22.5) return getString(R.string.windNE);
        return getString(R.string.windN);
    }


    private static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
