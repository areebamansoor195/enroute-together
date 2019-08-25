package com.example.areebamansoor.enroutetogether.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.areebamansoor.enroutetogether.DriverActivity;
import com.example.areebamansoor.enroutetogether.R;
import com.example.areebamansoor.enroutetogether.model.ActivePassengers;

import java.util.List;


public class PassengerRequestAdapter extends RecyclerView.Adapter<PassengerRequestAdapter.MyViewHolder> {

    private List<ActivePassengers> myPassengersList;
    private Context context;

    public PassengerRequestAdapter(Context context, List<ActivePassengers> activePassengers) {
        this.myPassengersList = activePassengers;
        this.context = context;
    }

    @NonNull
    @Override
    public PassengerRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_passenger_request_item, viewGroup, false);

        return new PassengerRequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        final ActivePassengers activePassengers = myPassengersList.get(i);

        if (myPassengersList.size() > 0) {
            holder.name.setText(activePassengers.getPassengerDetails().getName());
            holder.pickup.setText(context.getString(R.string.pickup_label)+ activePassengers.getPickup());
            holder.dropoff.setText(context.getString(R.string.dropoff_label)+activePassengers.getDropoff());
            holder.requestedSeats.setText(context.getString(R.string.seats_label)+ activePassengers.getRequestedSeats());

            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((DriverActivity) context).onAcceptRide(activePassengers);
                }
            });

            holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DriverActivity) context).onRejectRide(activePassengers);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return myPassengersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, pickup, dropoff, requestedSeats;
        Button acceptBtn, rejectBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.passenger_name);
            pickup = itemView.findViewById(R.id.pickup_address);
            dropoff = itemView.findViewById(R.id.dropoff_address);
            requestedSeats = itemView.findViewById(R.id.requestedSeats);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
        }
    }
}
