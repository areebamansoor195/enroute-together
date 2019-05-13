package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.areebamansoor.enroutetogether.databinding.ActivityConfirmRideBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.model.Vehicle;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.example.areebamansoor.enroutetogether.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVER;

public class ConfirmRide extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ConfirmRide";
    private ActivityConfirmRideBinding binding;
    private Vehicle vehicle;
    private ActiveDrivers activeDrivers;
    private SupportMapFragment mapFragment;
    private ValueEventListener valueEventListener;
    private ProgressDialog progressDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ConfirmRide.this, R.layout.activity_confirm_ride);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        binding.toolbarLayout.toolbar.setTitle("Confirm Ride");
        activeDrivers = (ActiveDrivers) getIntent().getSerializableExtra("active_driver");


        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmRide.this, EditVehicleActivity.class));
            }
        });

        binding.letsStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveDriver();
            }
        });
    }

    private void setActiveDriver() {

        if (activeDrivers != null) {

            progressDialog.show();

            activeDrivers.setId(user.getUserId());
            activeDrivers.setVehicleId(vehicle.getVehicleId());

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).removeEventListener(valueEventListener);
                    Log.e(TAG, "Child added");
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                }
            };
            Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).addListenerForSingleValueEvent(valueEventListener);
            Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(user.getUserId()).setValue(activeDrivers);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        vehicle = new Gson().fromJson(SharedPreferencHandler.getVehicle(), Vehicle.class);
        binding.carMake.setText("Name : " + vehicle.getName());
        binding.carCapacity.setText("Capacity : " + vehicle.getCapacity());
        binding.carColor.setText("Color : " + vehicle.getColor());
        binding.carModel.setText("Model : " + vehicle.getModel());
        binding.carPlateNumber.setText("Plate Number : " + vehicle.getPlateNumber());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");
        try {
            String[] sourceLatLngString = activeDrivers.getSourceLatLng().split(",");
            LatLng sourceLatLng = new LatLng(Double.parseDouble(sourceLatLngString[0]), Double.parseDouble(sourceLatLngString[1]));
            View source_marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
            ImageView pin_image = source_marker.findViewById(R.id.pin_img);
            pin_image.setImageDrawable(getResources().getDrawable(R.drawable.source_pin));
            TextView marker_text = source_marker.findViewById(R.id.marker_text);
            marker_text.setText("Source");
            googleMap.addMarker(new MarkerOptions()
                    .position(sourceLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(ConfirmRide.this, source_marker))));

            String[] destLatLngString = activeDrivers.getDestinationLatLng().split(",");
            LatLng destLatLng = new LatLng(Double.parseDouble(destLatLngString[0]), Double.parseDouble(destLatLngString[1]));
            View dest_marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
            ImageView pin_image1 = dest_marker.findViewById(R.id.pin_img);
            pin_image1.setImageDrawable(getResources().getDrawable(R.drawable.destination_pin));
            TextView marker_text1 = dest_marker.findViewById(R.id.marker_text);
            marker_text1.setText("Dest");
            googleMap.addMarker(new MarkerOptions()
                    .position(destLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(ConfirmRide.this, dest_marker))));

            List<LatLng> points = new ArrayList<>();
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(getResources().getColor(R.color.colorPrimary));
            polylineOptions.width(10);

            points.add(sourceLatLng);
            points.add(destLatLng);

            polylineOptions.addAll(points);

            googleMap.addPolyline(polylineOptions);

            adjustBounds(sourceLatLng, destLatLng, googleMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adjustBounds(LatLng sourceLocation, LatLng destinationLocation, GoogleMap map) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (sourceLocation != null)
            builder.include(sourceLocation);

        if (destinationLocation != null)
            builder.include(destinationLocation);

        LatLngBounds bounds = builder.build();

        int padding = 200;
        CameraUpdate cu = null;
        if (sourceLocation != null || destinationLocation != null) {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }

        map.animateCamera(cu);
    }
}
