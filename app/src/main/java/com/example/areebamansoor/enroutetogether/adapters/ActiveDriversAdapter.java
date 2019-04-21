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

public class ActiveDriversAdapter extends RecyclerView.Adapter<ActiveDriversAdapter.MyViewHolder> {

    private List<ActiveDrivers> activeDriversList;

    public ActiveDriversAdapter(List<ActiveDrivers> activeDriversList) {
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
        holder.driverName.setText("Driver's Name : ");
        holder.route.setText("Route : " + activeDrivers.getSource() + " to " + activeDrivers.getDestination());
    }

    @Override
    public int getItemCount() {
        return activeDriversList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView driverName, route;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            route = itemView.findViewById(R.id.route);
        }
    }
}
