package ru.tutudu.weatherapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class LocationsFragment extends Fragment{
    private static String LOCATIONS_TYPE = "locations_type";
    private boolean onlyFavorites;
    RecyclerView recyclerView;
    LinearLayoutManager llManager;
    ScrollAdapter adapter = new ScrollAdapter(new ArrayList<Location>(), null);
    View.OnClickListener mListener;
    Activity curActivity;


    public static LocationsFragment newInstance(boolean locationType) {
        final LocationsFragment fragment = new LocationsFragment();
        final Bundle args = new Bundle();
        args.putBoolean(LOCATIONS_TYPE, locationType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(LOCATIONS_TYPE)) {
            onlyFavorites = getArguments().getBoolean(LOCATIONS_TYPE);
        }
        else {
            throw new IllegalArgumentException("Creating LocationsFragment without params");
        }
    }


    public View.OnClickListener getUpdateListener() {
        Fragment mThis = this;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setReorderingAllowed(false);
                ft.detach(mThis).attach(mThis).commit();
            }
        };
    }

    public void setListener(View.OnClickListener onClickListener) {
        mListener = onClickListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        llManager = new LinearLayoutManager(getContext());

        if (!onlyFavorites) {
            adapter = new ScrollAdapter(App.getInstance().getDatabase().locationDao().getAll(),
                    getContext());
        }
        else {
            adapter = new ScrollAdapter(App.getInstance().getDatabase().locationDao().getAllFavorites(),
                    getContext());
        }
        adapter.setFragmentListener(mListener);
        adapter.setActivity(curActivity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llManager);
        return view;
    }


    public void setActivity(Activity locationsActivity) {
        curActivity = locationsActivity;
        adapter.setActivity(curActivity);
    }
}
