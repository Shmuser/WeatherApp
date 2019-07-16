package ru.tutudu.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


public class ScrollAdapter extends RecyclerView.Adapter {
    private int idOn, idOff;
    List<Location> data;
    Context context;


    public ScrollAdapter(List<Location> data, Context context) {
        idOn = context.getResources().getIdentifier("android:drawable/btn_star_big_on", null, null);
        idOff = context.getResources().getIdentifier("android:drawable/btn_star_big_off", null, null);
        this.data = data;
        this.context = context;
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
        vHolder.locLon.setText("lon:" + data.get(position).lon);
        vHolder.locLat.setText("lon:" + data.get(position).lat);
        if (data.get(position).isFavorite) {
            vHolder.locLike.setImageResource(idOn);
        }
        else {
            vHolder.locLike.setImageResource(idOff);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class VHolder extends RecyclerView.ViewHolder {
        TextView locName;
        TextView locLon;
        TextView locLat;
        ImageView locLike;

        public VHolder(View itemView) {
            super(itemView);
            locName = itemView.findViewById(R.id.location_name);
            locLon = itemView.findViewById(R.id.location_lon);
            locLat = itemView.findViewById(R.id.location_lat);
            locLike = itemView.findViewById(R.id.location_like);
        }
    }
}
