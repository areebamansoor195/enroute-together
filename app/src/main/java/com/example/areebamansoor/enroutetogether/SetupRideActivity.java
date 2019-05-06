package com.example.areebamansoor.enroutetogether;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.areebamansoor.enroutetogether.databinding.ActivitySetupRideBinding;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.example.areebamansoor.enroutetogether.model.Vehicle;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.gson.Gson;

public class SetupRideActivity extends AppCompatActivity {

    private ActivitySetupRideBinding binding;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup_ride);

        binding.toolbarLayout.toolbar.setTitle("Setup Ride");

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetupRideActivity.this, EditVehicleActivity.class));
            }
        });


        binding.letsStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Insert user in active driver table and go to maps screen
                String source = binding.etSource.getText().toString();
                String dest = binding.etDestination.getText().toString();
                String sourceLatLng = "24.8845719,67.1731622";  //Malir Halt
                String destLatLng = "24.9251347,67.0277015";    //Jinnah Uni


                ActiveDrivers activeDriver = new ActiveDrivers();
                /*activeDriver.setPickup(source);
                activeDriver.setDropoff(dest);
                activeDriver.setPickupLatLng(sourceLatLng);
                activeDriver.setDropoffLatLng(destLatLng);*/

                Intent intent = new Intent(SetupRideActivity.this, OfferRideActivity.class);
                intent.putExtra("active_driver", activeDriver);
                startActivity(intent);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        vehicle = new Gson().fromJson(SharedPreferencHandler.getVehicle(), Vehicle.class);

        binding.carMake.setText("Make : " + vehicle.getMaker());
        binding.carModel.setText("Model : " + vehicle.getModel());
        binding.carColor.setText("Color : " + vehicle.getColor());
        binding.carCapacity.setText("Capacity : " + vehicle.getCapacity());
        binding.carPlateNumber.setText("Plate Number : " + vehicle.getPlateNumber());

    }

}
