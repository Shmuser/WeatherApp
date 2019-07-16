package ru.tutudu.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LocationsFragment extends Fragment {
    private static String LOCATIONS_TYPE = "locations_type";
    private boolean onlyFavorites;
    RecyclerView recyclerView;
    LinearLayoutManager llManager;
    ScrollAdapter adapter;


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
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llManager);

        return view;
    }

}
