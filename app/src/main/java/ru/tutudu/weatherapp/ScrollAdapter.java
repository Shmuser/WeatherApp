package ru.tutudu.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class ScrollAdapter extends RecyclerView.Adapter {
    private int idOn, idOff;
    private List<Location> data;
    private Context context;
    private View.OnClickListener fragListener;
    private Activity currentActivity;


    void setFragmentListener (View.OnClickListener onClickListener) {
        fragListener = onClickListener;
    }


    ScrollAdapter(List<Location> data, Context context) {
        if (context != null) {
            idOn = context.getResources().getIdentifier("android:drawable/btn_star_big_on", null, null);
            idOff = context.getResources().getIdentifier("android:drawable/btn_star_big_off", null, null);
            this.data = data;
            this.context = context;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.location_view, parent, false);
        return new VHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        vHolder.locName.setText(data.get(position).name);
        String country = data.get(position).country;
        String city = data.get(position).city;
        if (country != null)
            if (city != null)
                vHolder.locCity.setText(city + ", " + country);
            else
                vHolder.locCity.setText(country);
        if (data.get(position).isFavorite) {
            vHolder.locLike.setImageResource(idOn);
        }
        else {
            vHolder.locLike.setImageResource(idOff);
        }
        vHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        WeatherActivity.class);
                intent.putExtra("lat", data.get(position).lat);
                intent.putExtra("lon", data.get(position).lon);
                intent.putExtra("name", data.get(position).name);
                intent.putExtra("city", data.get(position).city);
                intent.putExtra("country", data.get(position).country);
                intent.putExtra("isFav", data.get(position).isFavorite);

                context.startActivity(intent);
            }
        });
        vHolder.locLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location updatedLoc = data.get(position);
                updatedLoc.isFavorite = !updatedLoc.isFavorite;
                App.getInstance().getDatabase().locationDao().update(updatedLoc);
                fragListener.onClick(null);
            }
        });
        vHolder.locDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(currentActivity)
                        .setTitle(currentActivity.getResources().getString(R.string.delete_dialog_title))
                        .setMessage(currentActivity.getResources().getString(R.string.delete_dialog_message))
                        .setIcon(R.drawable.alert_circle)
                        .setPositiveButton(currentActivity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int buttonPos) {
                                App.getInstance().getDatabase().locationDao().delete(data.get(position));
                                fragListener.onClick(null);
                            }})
                        .setNegativeButton(currentActivity.getResources().getString(R.string.cancel), null).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    void setActivity(Activity locationsActivity) {
        currentActivity = locationsActivity;
    }


    public class VHolder extends RecyclerView.ViewHolder{
        TextView locName;
        TextView locCity;
        ImageView locLike;
        LinearLayout linearLayout;
        ImageView locDelete;


        VHolder(View itemView) {
            super(itemView);
            locName = itemView.findViewById(R.id.location_name);
            locCity = itemView.findViewById(R.id.location_city);
            locLike = itemView.findViewById(R.id.location_like);
            linearLayout = itemView.findViewById(R.id.location_layout);
            locDelete = itemView.findViewById(R.id.location_delete);
        }
    }
}
