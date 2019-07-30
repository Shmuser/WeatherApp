package ru.tutudu.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import moe.feng.common.stepperview.VerticalStepperItemView;

public class StepperActivity extends AppCompatActivity {

    private VerticalStepperItemView mSteppers[] = new VerticalStepperItemView[3];
    private TextView addLocName, locPos;
    private GoogleMap gMap;
    private static final int PERMISSIONS_REQUEST_CURRENT_LOC = 12;
    private boolean permGranted = false;
    private MarkerOptions position = new MarkerOptions();
    private ImageView locImage;
    private CheckBox isFav;
    Subject<LatLng> posObservable = PublishSubject.create();
    Subject<Boolean> needToAlert = PublishSubject.create();
    LatLng choosedLocLatLng;
    Geocoder geocoder;
    String locCity, locCountry;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stepper_layout);
        isFav = findViewById(R.id.add_is_fav);
        locImage = findViewById(R.id.new_loc_ready);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        locPos = findViewById(R.id.new_loc_position);
        posObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LatLng>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(LatLng latLng) {
                        fillLocName(latLng);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onComplete() {
                    }
                });

        mSteppers[0] = findViewById(R.id.stepper_0);
        mSteppers[1] = findViewById(R.id.stepper_1);
        mSteppers[2] = findViewById(R.id.stepper_2);
        VerticalStepperItemView.bindSteppers(mSteppers);

        addLocName = findViewById(R.id.add_loc_name);
        Button mNextBtn0 = findViewById(R.id.button_next_0);
        mNextBtn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addLocName.getText().toString().isEmpty()) {
                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    mSteppers[0].setSummaryFinished(addLocName.getText().toString());
                    mSteppers[0].nextStep();
                } else {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_error);
                    addLocName.startAnimation(anim);
                    addLocName.setHint(getResources().getString(R.string.stepperHint));
                }
            }
        });

        Button mNextBtn1 = findViewById(R.id.button_next_1);
        mNextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locImage.getVisibility() == View.VISIBLE) {
                    mSteppers[1].setSummaryFinished(locPos.getText());
                    mSteppers[1].nextStep();
                }
            }
        });

        Button mNextBtn2 = findViewById(R.id.button_next_2);
        mNextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location newLoc = new Location();
                newLoc.isFavorite = isFav.isChecked();
                newLoc.name = addLocName.getText().toString();
                newLoc.lat = Double.parseDouble(new DecimalFormat("##.###")
                        .format(choosedLocLatLng.latitude)
                        .replace(",", "."));
                newLoc.lon = Double.parseDouble(new DecimalFormat("##.###")
                        .format(choosedLocLatLng.longitude)
                        .replace(",", "."));
                newLoc.city = locCity;
                newLoc.country = locCountry;
                try {
                    App.getInstance().getDatabase().locationDao().insert(newLoc);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.stepperException),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        Button mPrevBtn1 = findViewById(R.id.button_prev_1);
        mPrevBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[1].prevStep();
            }
        });

        Button mPrevBtn2 = findViewById(R.id.button_prev_2);
        mPrevBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSteppers[2].prevStep();
            }
        });

        findViewById(R.id.choose_loc_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choosedLocLatLng != null) {
                    locImage.setVisibility(View.VISIBLE);

                    if (locCountry != null) {
                        if (locCity != null) {
                            locPos.setText(locCity + ",\n" + locCountry);
                        }
                        else {
                            locPos.setText(locCountry);
                        }
                    }
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map_fragment);

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
                        Context context = getApplicationContext();
                        needToAlert.onNext(!connected);
                    }
                });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapInit(googleMap);
            }
        });
    }


    private Observable<Boolean> executeNetworkCall() {
        return App.isInternetOn(getApplicationContext())
                .filter(connectionStatus -> connectionStatus);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }


    private void fillLocName(LatLng latLng) {
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude,1);
            if (addressList.size() > 0) {
                locCountry = addressList.get(0).getCountryName();
                locCity = addressList.get(0).getLocality();
            }
            choosedLocLatLng = latLng;
        } catch (IOException e) {
            locCountry = "Error to find location name";
            locImage.setVisibility(View.INVISIBLE);
        }
    }


    private void mapInit(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.7507, 37.6177), 7));
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                posObservable.onNext(latLng);
                position.position(latLng);
                gMap.clear();
                gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.addMarker(position);
            }
        });
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CURRENT_LOC);
            return;
        }
        permGranted = true;
        updateLocationUI();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CURRENT_LOC: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (permGranted) {
            gMap.setMyLocationEnabled(true);
            gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    LocationManager locationManager = (LocationManager)
                            getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    android.location.Location location = locationManager.getLastKnownLocation(locationManager
                            .getBestProvider(criteria, false));
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng newPos = new LatLng(latitude, longitude);
                        posObservable.onNext(newPos);
                        position.position(newPos);
                        gMap.clear();
                        gMap.animateCamera(CameraUpdateFactory.newLatLng(newPos));
                        gMap.addMarker(position);
                    }
                    return true;
                }
            });
        }
    }
}
