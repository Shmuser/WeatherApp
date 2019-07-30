package ru.tutudu.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class LocationsActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static int ADD_LOCATION_REQUEST = 1;

   /* static Context getCurContext() {
        return this;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StepperActivity.class);
                startActivityForResult(intent, ADD_LOCATION_REQUEST);
            }
        });
        ImageView languageImage = findViewById(R.id.language_button);

        if (LocaleChanger.getLanguage(getApplicationContext()).equals("ru")) {
            languageImage.setImageDrawable(getDrawable(R.drawable.ru_flag));
            languageImage.setTag("ru");
        } else {
            languageImage.setImageDrawable(getDrawable(R.drawable.gb_flag));
            languageImage.setTag("en");
        }

        LocationsActivity mActivity = this;
        findViewById(R.id.language_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)v;
              //  imageView.setT
                if (imageView == null || imageView.getTag() == null)
                    return;
                if (imageView.getTag().equals("ru")) {
                    Log.d("bubgg", "to en");
                    imageView.setImageDrawable(getDrawable(R.drawable.gb_flag));
                    //item.setIcon(R.drawable.gb_flag);
                    LocaleChanger.setLocale(getApplicationContext(), "en");
                }
                else {
                    Log.d("bubgg", "to ru");
                    imageView.setImageDrawable(getDrawable(R.drawable.ru_flag));
                    //item.setIcon(R.drawable.gb_flag);
                    LocaleChanger.setLocale(getApplicationContext(), "ru");
                }
                mActivity.recreate();
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                setupViewPager(viewPager);
            }
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }


 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_locations, menu);
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_lang) {
            if (item.getIcon().equals(getDrawable(R.drawable.ru_flag))) {
                Log.d("bubgg", "to en");
                item.setIcon(R.drawable.gb_flag);
                LocaleChanger.setLocale(this, "en");
            }
            else {
                Log.d("bubgg", "to ru");
                item.setIcon(R.drawable.ru_flag);
                LocaleChanger.setLocale(this, "ru");
            }
            recreate();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        LocationsFragment allLocFrag = LocationsFragment.newInstance(false);
        LocationsFragment likedLocFrag = LocationsFragment.newInstance(true);
        View.OnClickListener updateFrags = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allLocFrag.getUpdateListener().onClick(null);
                likedLocFrag.getUpdateListener().onClick(null);
            }
        };
        allLocFrag.setListener(updateFrags);
        likedLocFrag.setListener(updateFrags);
        allLocFrag.setActivity(this);
        likedLocFrag.setActivity(this);
        adapter.addFragment(allLocFrag, getResources().getString(R.string.alocations_frag1title));
        adapter.addFragment(likedLocFrag, getResources().getString(R.string.alocations_frag2title));
        viewPager.setAdapter(adapter);
    }
}
