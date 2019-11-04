package com.juw.areebamansoor.enroutetogether;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juw.areebamansoor.enroutetogether.adapters.PassengerRequestAdapter;
import com.juw.areebamansoor.enroutetogether.databinding.ActivityDriverBinding;
import com.juw.areebamansoor.enroutetogether.firebase.Firebase;
import com.juw.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.juw.areebamansoor.enroutetogether.model.ActivePassengers;
import com.juw.areebamansoor.enroutetogether.model.User;
import com.juw.areebamansoor.enroutetogether.utils.DataParser;
import com.juw.areebamansoor.enroutetogether.utils.FcmCallback;
import com.juw.areebamansoor.enroutetogether.utils.MapAnimator;
import com.juw.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.juw.areebamansoor.enroutetogether.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static com.juw.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;
import static com.juw.areebamansoor.enroutetogether.utils.Constants.ACTIVE_PASSENGERS;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "DriverActivity";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private ActivityDriverBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayList<Polyline> polylineArrayList;
    private LatLng sourceLocation, destinationLocation;
    private ActiveDrivers myOfferRide;
    private ProgressDialog progressDialog;

    private LatLng myLatLng;

    //private ValueEventListener passengersListener;
    private DatabaseReference activeDriverRef;
    private ValueEventListener valueEventListener;
    private User user;
    List<ActivePassengers> passengersList;
    List<ActivePassengers> acceptedPassengersList;
    private Dialog dialog;
    private List<Marker> passengerMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver);

        binding.toolbarLayout.toolbar.setTitle("My Offer Ride");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        if (!Utils.isLocationEnabled(this)) {
            alertForNoGps();
        }

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);

        myOfferRide = new Gson().fromJson(getIntent().getStringExtra("Job"), ActiveDrivers.class);
        String[] sourceLatLngStr = myOfferRide.getSourceLatLng().split(",");
        String[] destLatLngStr = myOfferRide.getDestinationLatLng().split(",");

        sourceLocation = new LatLng(Double.parseDouble(sourceLatLngStr[0]), Double.parseDouble(sourceLatLngStr[1]));
        destinationLocation = new LatLng(Double.parseDouble(destLatLngStr[0]), Double.parseDouble(destLatLngStr[1]));

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (myOfferRide.getPassengerRequests() != null) {
            String[] requests = myOfferRide.getPassengerRequests().split(",");
            Log.e(TAG, requests.length + "");
            binding.notifications.setText(requests.length + "");
        }

        activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(user.getUserId());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActiveDrivers activeDrivers = null;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    activeDrivers = data.getValue(ActiveDrivers.class);
                }
                myOfferRide = activeDrivers;
                if (activeDrivers.getPassengerRequests() == null) {
                    binding.notifications.setText("0");
                    return;
                }
                String[] requestCount = activeDrivers.getPassengerRequests().split(",");
                binding.notifications.setText(requestCount.length + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        activeDriverRef.addValueEventListener(valueEventListener);

        binding.showRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.notifications.getText().toString().equalsIgnoreCase("0"))
                    showRequestList();
            }
        });
    }

    private void showRequestList() {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_passenger_requests);

        passengersList = new ArrayList<>();
        passengersList.clear();

        ProgressBar progressBar = dialog.findViewById(R.id.progress);
        final RecyclerView passenger_request_list = dialog.findViewById(R.id.passenger_request_list);

        String[] requests = myOfferRide.getPassengerRequests().split(",");

        ValueEventListener valueEventListener;

        for (String passengerId : requests) {
            final DatabaseReference activePassengerRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS).child(passengerId);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ActivePassengers activePassengers = null;

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        activePassengers = data.getValue(ActivePassengers.class);
                    }
                    passengersList.add(activePassengers);
                    Log.e(TAG, passengersList.size() + "");

                    PassengerRequestAdapter adapter = new PassengerRequestAdapter(DriverActivity.this, passengersList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DriverActivity.this);
                    passenger_request_list.setLayoutManager(mLayoutManager);
                    passenger_request_list.setItemAnimator(new DefaultItemAnimator());
                    passenger_request_list.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            activePassengerRef.addValueEventListener(valueEventListener);
        }


        progressBar.setVisibility(GONE);
        /*
         */

        Window window = dialog.getWindow();
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);

        dialog.show();
    }

    public void onAcceptRide(ActivePassengers activePassengers) {
        progressDialog.show();
        FcmCallback fcmCallback = new FcmCallback() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(DriverActivity.this, "Request accepted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String reponse) {
            }
        };

        Utils.sendFCM(activePassengers.getFcmDeviceId(), RequestTypes.ACCEPT_REQUEST,
                "Hello " + activePassengers.getPassengerDetails().getName(), user.getName() + " accepted your ride request", user.getUserId(), fcmCallback);

        saveAcceptedPassengers(activePassengers);
    }

    public void onRejectRide(ActivePassengers activePassengers) {

    }

    private void saveAcceptedPassengers(final ActivePassengers activePassengers) {

        final DatabaseReference activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(user.getUserId());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeDriverRef.removeEventListener(valueEventListener);
                Log.e(TAG, dataSnapshot.getChildrenCount() + "");

                List<ActiveDrivers> myOfferRides = new ArrayList<>();


                /* Remove requested Passengers from firebase  */
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ActiveDrivers activeDriverTemp = data.getValue(ActiveDrivers.class);
                    String[] requested = activeDriverTemp.getPassengerRequests().split(",");

                    if (requested.length == 1) {
                        activeDriverTemp.setPassengerRequests(null);
                    } else {
                        StringBuilder newRequestedList = new StringBuilder();

                        for (int i = 0; i < requested.length; i++) {
                            if (!requested[i].equalsIgnoreCase(activePassengers.getUserId())) {
                                newRequestedList.append(requested[i] + ",");
                            }
                        }

                        String newList = newRequestedList.toString();

                        newList = newList.substring(0, newList.length() - 1);
                        activeDriverTemp.setPassengerRequests(newList);
                    }

                    int availableSeats = Integer.parseInt(activeDriverTemp.getAvailableSeats()) - Integer.parseInt(activePassengers.getRequestedSeats());

                    activeDriverTemp.setAvailableSeats(String.valueOf(availableSeats));

                    myOfferRide = activeDriverTemp;
                    myOfferRides.add(activeDriverTemp);
                }


                /* Add accepted Passengers in firebase  */
                if (myOfferRides.get(0).getAcceptedPassengers() == null) {
                    myOfferRides.get(0).setAcceptedPassengers(activePassengers.getUserId());
                } else {
                    myOfferRides.get(0).setAcceptedPassengers(myOfferRides.get(0).getAcceptedPassengers() + "," + activePassengers.getUserId());
                }


                activeDriverRef.setValue(myOfferRides).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (dialog != null)
                            dialog.dismiss();

                        if (myOfferRide.getAcceptedPassengers() != null) {
                            drawAcceptedPassengers();
                            activePassengers.setRideAccepted(true);
                            updateActivePassenger(null, activePassengers);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activeDriverRef.removeEventListener(valueEventListener);
            }
        };
        activeDriverRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activeDriverRef.removeEventListener(valueEventListener);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkLocationPermission() {

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void alertForNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void adjustBounds(List<Marker> driverPosition) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (sourceLocation != null)
            builder.include(sourceLocation);

        if (destinationLocation != null)
            builder.include(destinationLocation);

        if (driverPosition != null) {
            for (Marker marker : driverPosition) {
                builder.include(marker.getPosition());
            }
        }


        LatLngBounds bounds = builder.build();


        // begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen

        CameraUpdate cu = null;
        if (sourceLocation != null || destinationLocation != null) {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        } else {
            cu = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
        }

        mMap.animateCamera(cu);
    }

    private void adjustBounds() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (sourceLocation != null)
            builder.include(sourceLocation);

        if (destinationLocation != null)
            builder.include(destinationLocation);

        LatLngBounds bounds = builder.build();


        // begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen

        CameraUpdate cu = null;
        if (sourceLocation != null || destinationLocation != null) {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        } else {
            cu = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
        }

        mMap.animateCamera(cu);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected");
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e(TAG, myLatLng.latitude + "," + myLatLng.longitude);
            myOfferRide.setCurrentLatlng(myLatLng.latitude + "," + myLatLng.longitude);

            final DatabaseReference activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(user.getUserId());
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activeDriverRef.removeEventListener(valueEventListener);
                    Log.e(TAG, dataSnapshot.getChildrenCount() + "");

                    List<ActiveDrivers> myOfferRides = new ArrayList<>();


                    /* Remove requested Passengers from firebase  */
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ActiveDrivers activeDriverTemp = data.getValue(ActiveDrivers.class);
                        activeDriverTemp.setCurrentLatlng(myOfferRide.getCurrentLatlng());
                        myOfferRides.add(activeDriverTemp);
                    }

                    activeDriverRef.setValue(myOfferRides).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activeDriverRef.removeEventListener(valueEventListener);
                }
            };
            activeDriverRef.addListenerForSingleValueEvent(valueEventListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        acceptedPassengersList = new ArrayList<>();
        if (myOfferRide.getAcceptedPassengers() != null)
            drawAcceptedPassengers();
    }

    private void drawAcceptedPassengers() {

        Log.e(TAG, new Gson().toJson(myOfferRide));

        for (Marker marker : passengerMarkers) {
            marker.remove();
        }
        passengerMarkers.clear();

        final String[] acceptedPassengers = myOfferRide.getAcceptedPassengers().split(",");

        ValueEventListener valueEventListener;

        final DatabaseReference activePassengerRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ActivePassengers activePassengers = null;

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    for (DataSnapshot dataSnapshot1 : data.getChildren()) {
                        activePassengers = dataSnapshot1.getValue(ActivePassengers.class);

                        for (String accceptedPassenger : acceptedPassengers) {
                            if (activePassengers.getUserId().equalsIgnoreCase(accceptedPassenger) && !activePassengers.getCashCollected()) {

                                String initialLatLng = activePassengers.getPickupLatLng();
                                String[] latLngString = initialLatLng.split(",");
                                LatLng passengerPosition = new LatLng(Double.parseDouble(latLngString[0]), Double.parseDouble(latLngString[1]));
                                View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

                                if (activePassengers.getPassengerDetails().getImage_url() != null) {
                                    CircleImageView profile_img = marker.findViewById(R.id.profileImage);
                                    Log.e(TAG, activePassengers.getPassengerDetails().getImage_url());
                                    Picasso.get()
                                            .load(activePassengers.getPassengerDetails().getImage_url())
                                            .placeholder(R.drawable.icon2)
                                            .into(profile_img);
                                }

                                Marker passengerMarker = mMap.addMarker(new MarkerOptions()
                                        .position(passengerPosition)
                                        .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(DriverActivity.this, marker))));
                                passengerMarker.setTag(passengerMarkers.size());
                                passengerMarkers.add(passengerMarker);

                                adjustBounds(passengerMarkers);

                                acceptedPassengersList.add(activePassengers);
                            }

                        }
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        activePassengerRef.addValueEventListener(valueEventListener);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "You denied permissions", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);

                }
                return;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            ActivePassengers passengerDetails = acceptedPassengersList.get((int) marker.getTag());
            if (passengerDetails != null) {
                Log.e(TAG, passengerDetails.getPassengerDetails().getName());
                openDialog(passengerDetails, marker);
            }
        }
        return false;
    }

    private void openDialog(final ActivePassengers passenger, final Marker marker) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_passenger_details);

        final Button btn_action_ride = dialog.findViewById(R.id.btn_action_ride);
        final Button btn_collect_fare = dialog.findViewById(R.id.btn_collect_fare);
        TextView name = dialog.findViewById(R.id.driverName);
        TextView gender = dialog.findViewById(R.id.gender);
        TextView source = dialog.findViewById(R.id.source);
        TextView destination = dialog.findViewById(R.id.destination);
        TextView availableSeats = dialog.findViewById(R.id.availableSeats);
        final CircleImageView profileImage = dialog.findViewById(R.id.profile_image);
        ImageView closeBtn = dialog.findViewById(R.id.close_btn);

        final RelativeLayout ride_controll = dialog.findViewById(R.id.ride_controll);
        final RelativeLayout fare_controll = dialog.findViewById(R.id.fare_controll);

        TextView totalFare = dialog.findViewById(R.id.totalFare);
        final EditText passenger_paid = dialog.findViewById(R.id.et_fare);


        name.setText(passenger.getPassengerDetails().getName());
        gender.setText(passenger.getPassengerDetails().getGender());
        source.setText(passenger.getPickup());
        destination.setText(passenger.getDropoff());
        availableSeats.setText(passenger.getRequestedSeats() + " Seat(s)");

        if (passenger.getPassengerDetails().getImage_url() != null) {
            Picasso.get()
                    .load(passenger.getPassengerDetails().getImage_url())
                    .placeholder(R.drawable.icon2)
                    .into(profileImage);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (passenger.getRideArrive()) {
            btn_action_ride.setText("Start Ride");
            btn_action_ride.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        if (passenger.getRideStart()) {
            btn_action_ride.setText("End Ride");
            btn_action_ride.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        if (passenger.getRideEnd()) {
            ride_controll.setVisibility(GONE);
            fare_controll.setVisibility(View.VISIBLE);
            totalFare.setText("Total Fare : " + passenger.getFare().trim() + " Rs");
        }

        if (passenger.getCashCollected()) {
            fare_controll.setVisibility(GONE);
            ride_controll.setVisibility(View.VISIBLE);
            btn_action_ride.setVisibility(GONE);
        }

        btn_collect_fare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passenger_paid.getText().length() > 0) {
                    progressDialog.show();
                    FcmCallback fcmCallback = new FcmCallback() {
                        @Override
                        public void onResponse(String response) {
                            passenger.setCashCollected(true);
                            updateActivePassenger(dialog, passenger);
                            marker.remove();
                        }

                        @Override
                        public void onFailure(String reponse) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(DriverActivity.this, "Unable to end ride", Toast.LENGTH_SHORT).show();
                        }
                    };
                    Utils.sendFCM(passenger.getFcmDeviceId(), RequestTypes.RIDE_END,
                            "Hello " + passenger.getPassengerDetails().getName(), "Payment received successfully. Thank you!", user.getUserId(), fcmCallback);
                } else {
                    Toast.makeText(DriverActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_action_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (btn_action_ride.getText().toString().equalsIgnoreCase("I'm Here")) {
                    progressDialog.show();
                    FcmCallback fcmCallback = new FcmCallback() {
                        @Override
                        public void onResponse(String response) {
                            passenger.setRideArrive(true);
                            updateActivePassenger(dialog, passenger);
                        }

                        @Override
                        public void onFailure(String reponse) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(DriverActivity.this, "Unable to arrive", Toast.LENGTH_SHORT).show();
                        }
                    };
                    Utils.sendFCM(passenger.getFcmDeviceId(), RequestTypes.RIDE_ARRIVE,
                            "Hello " + passenger.getPassengerDetails().getName(), "Your driver " + user.getName() + " is just arrived!", user.getUserId(), fcmCallback);

                } else if (btn_action_ride.getText().toString().equalsIgnoreCase("Start Ride")) {
                    progressDialog.show();
                    FcmCallback fcmCallback = new FcmCallback() {
                        @Override
                        public void onResponse(String response) {
                            passenger.setRideStart(true);
                            /*String[] dropOffLatLngStr = passenger.getDropoffLatLng().split(",");
                            LatLng dropOffLatLng = new LatLng(Double.parseDouble(dropOffLatLngStr[0]), Double.parseDouble(dropOffLatLngStr[1]));
                            passengerMarkers.get(acceptedPassengersList.indexOf(passenger)).setPosition(dropOffLatLng);*/
                            updateActivePassenger(dialog, passenger);
                        }

                        @Override
                        public void onFailure(String reponse) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(DriverActivity.this, "Unable to start ride", Toast.LENGTH_SHORT).show();
                        }
                    };
                    Utils.sendFCM(passenger.getFcmDeviceId(), RequestTypes.RIDE_START,
                            "Hello " + passenger.getPassengerDetails().getName(), "Your ride has been started!", user.getUserId(), fcmCallback);
                } else if (btn_action_ride.getText().toString().equalsIgnoreCase("End Ride")) {
                    progressDialog.show();
                    FcmCallback fcmCallback = new FcmCallback() {
                        @Override
                        public void onResponse(String response) {
                            passenger.setRideEnd(true);
                            updateActivePassenger(dialog, passenger);
                            ride_controll.setVisibility(View.GONE);
                            fare_controll.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(String reponse) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(DriverActivity.this, "Unable to end ride", Toast.LENGTH_SHORT).show();
                        }
                    };
                    Utils.sendFCM(passenger.getFcmDeviceId(), RequestTypes.RIDE_END,
                            "Hello " + passenger.getPassengerDetails().getName(), "Your ride has been ended. Your Fare is " + passenger.getFare() + " Rs.", user.getUserId(), fcmCallback);
                }
            }
        });


        dialog.show();

    }

    private void updateActivePassenger(final Dialog dialog, final ActivePassengers passenger) {

        final DatabaseReference activePassengerRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS).child(passenger.getUserId());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                activePassengerRef.removeEventListener(valueEventListener);

                Log.e(TAG, dataSnapshot.getChildrenCount() + "");
                List<ActivePassengers> myBookRides = new ArrayList<>();

                myBookRides.add(passenger);

                Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).child(passenger.getUserId()).
                        setValue(myBookRides).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (dialog != null)
                            dialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activePassengerRef.removeEventListener(valueEventListener);
            }
        };
        activePassengerRef.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 13));

        mMap.setOnMarkerClickListener(this);

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
        ImageView pin_image = marker.findViewById(R.id.pin_img);
        pin_image.setImageDrawable(getResources().getDrawable(R.drawable.source_pin));
        TextView marker_text = marker.findViewById(R.id.marker_text);
        marker_text.setText("Source");

        mMap.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(DriverActivity.this, marker))));

        View Destmarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
        ImageView pin_image_d = Destmarker.findViewById(R.id.pin_img);
        pin_image_d.setImageDrawable(getResources().getDrawable(R.drawable.destination_pin));
        TextView marker_text_d = Destmarker.findViewById(R.id.marker_text);
        marker_text_d.setText("Dest");

        mMap.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(DriverActivity.this, Destmarker))));

        drawRoute(myOfferRide.getSourceLatLng(), myOfferRide.getDestinationLatLng());

        adjustBounds();


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //  getPassengerRequests();
    }

    private void getPassengerRequests() {

        progressDialog.show();

       /* passengersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                Log.e(TAG, "Active Passengers = " + dataSnapshot.getChildrenCount() + "");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).addValueEventListener(passengersListener);*/

    }

    private void showPassengers() {
        if (myOfferRide.getPassengerRequests() != null) {
            Log.e(TAG, "Passengers = " + myOfferRide.getPassengerRequests());
            String[] passengerIds = myOfferRide.getPassengerRequests().split(",");


            for (int i = 0; i < passengerIds.length; i++) {

            }
        }
    }


    /*-------------------Draw Route---------------------*/


    private void drawRoute(String s, String d) {
        String[] source = s.split(",");
        String[] destination = d.split(",");
        LatLng sourceLatLng = new LatLng(Double.parseDouble(source[0]), Double.parseDouble(source[1]));
        LatLng destinationLatLng = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));

        String URL = getUrl(sourceLatLng, destinationLatLng);
        Log.e("MAP_URL", URL);
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(URL);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + "key=AIzaSyA_jKH6Gch6F27l2kCTwflCJUZ9nXObWGQ";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void startAnim(ArrayList<LatLng> route) {
        if (mMap != null) {
            polylineArrayList = MapAnimator.getInstance().animateRoute(mMap, route);
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                ParserTask parserTask = new ParserTask();
                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser(DriverActivity.this);

                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            final ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";
            try {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            distance = (String) point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(7);
                    lineOptions.color(getResources().getColor(android.R.color.black));

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                startAnim(points);
            } else {
            }
        }
    }
}
