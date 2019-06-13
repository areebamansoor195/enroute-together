package com.example.areebamansoor.enroutetogether.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.areebamansoor.enroutetogether.R;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;

import java.util.List;

public class OfferRidesAdapter extends RecyclerView.Adapter<OfferRidesAdapter.MyViewHolder> {

    private List<ActiveDrivers> activeDriversList;

    public OfferRidesAdapter(List<ActiveDrivers> activeDriversList) {
        this.activeDriversList = activeDriversList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_active_driver, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        ActiveDrivers activeDrivers = activeDriversList.get(i);
        holder.source.setText(activeDrivers.getSource());
        holder.destination.setText(activeDrivers.getDestination());
        holder.dateTime.setText(activeDrivers.getTimeStamp().split("\\.")[0]);
    }

    @Override
    public int getItemCount() {
        return activeDriversList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView source, destination, dateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.source);
            destination = itemView.findViewById(R.id.destination);
            dateTime = itemView.findViewById(R.id.date_time);
        }
    }
}
