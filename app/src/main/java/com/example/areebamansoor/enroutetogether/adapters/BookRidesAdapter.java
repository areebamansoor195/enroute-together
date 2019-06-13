package com.example.areebamansoor.enroutetogether.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.areebamansoor.enroutetogether.R;
import com.example.areebamansoor.enroutetogether.model.ActivePassengers;

import java.util.List;

public class BookRidesAdapter extends RecyclerView.Adapter<BookRidesAdapter.MyViewHolder> {

    private List<ActivePassengers> activePassengersList;

    public BookRidesAdapter(List<ActivePassengers> activePassengersList) {
        this.activePassengersList = activePassengersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_active_passenger, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        ActivePassengers activePassengers = activePassengersList.get(i);
        holder.pickup.setText(activePassengers.getPickup());
        holder.dropoff.setText(activePassengers.getDropoff());
        holder.dateTime.setText(activePassengers.getTimeStamp().split("\\.")[0]);
        holder.requestedSeats.setText(activePassengers.getRequestedSeats()+" Seat(s)");
    }

    @Override
    public int getItemCount() {
        return activePassengersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView pickup, dropoff, dateTime, requestedSeats;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pickup = itemView.findViewById(R.id.pickup);
            dropoff = itemView.findViewById(R.id.dropoff);
            dateTime = itemView.findViewById(R.id.date_time);
            requestedSeats = itemView.findViewById(R.id.requestedSeats);
        }
    }
}
