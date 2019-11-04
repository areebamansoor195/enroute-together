package com.juw.areebamansoor.enroutetogether.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.juw.areebamansoor.enroutetogether.R;
import com.juw.areebamansoor.enroutetogether.fragments.FragmentOfferRides;
import com.juw.areebamansoor.enroutetogether.model.ActiveDrivers;

import java.util.List;

public class OfferRidesAdapter extends RecyclerView.Adapter<OfferRidesAdapter.MyViewHolder> {

    private List<ActiveDrivers> activeDriversList;
    private FragmentOfferRides fragmentOfferRides;

    public OfferRidesAdapter(FragmentOfferRides fragmentOfferRides, List<ActiveDrivers> activeDriversList) {
        this.activeDriversList = activeDriversList;
        this.fragmentOfferRides = fragmentOfferRides;
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
        final ActiveDrivers activeDrivers = activeDriversList.get(i);
        holder.source.setText(activeDrivers.getSource());
        holder.destination.setText(activeDrivers.getDestination());
        holder.dateTime.setText(activeDrivers.getTimeStamp().split("\\.")[0]);


        if (activeDrivers.getPassengerRequests() != null) {
            if (!activeDrivers.getPassengerRequests().contains(",")) {
                holder.notifications.setText("1");
            } else {
                String[] passengers = activeDrivers.getPassengerRequests().split(",");
                Log.e("Offer ride adapter", passengers.length + "");
                holder.notifications.setText(passengers.length + "");
            }
        } else {
            holder.notification_view.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentOfferRides.onClickItem(activeDrivers);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activeDriversList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView source, destination, dateTime, notifications;
        private FrameLayout notification_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.source);
            destination = itemView.findViewById(R.id.destination);
            dateTime = itemView.findViewById(R.id.date_time);
            notifications = itemView.findViewById(R.id.notifications);
            notification_view = itemView.findViewById(R.id.notification_view);
        }
    }
}
