package com.example.areebamansoor.enroutetogether;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityBookRideBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.example.areebamansoor.enroutetogether.model.ActivePassengers;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.utils.DataParser;
import com.example.areebamansoor.enroutetogether.utils.MapAnimator;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.example.areebamansoor.enroutetogether.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_PASSENGERS;

public class BookRideActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "BookRideActivity";
    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    private long passenger_id = 0;
    private ActivityBookRideBinding binding;
    private GoogleMap mMap;
    private LatLng myLatLng;
    private LatLng latLng;
    private ActivePassengers activePassengers;
    private SupportMapFragment mapFragment;
    private PlaceAutocompleteFragment autocompleteFragment;
    private User user;
    private Geocoder geocoder;
    private List<Address> addresses;

    private ArrayList<Polyline> polylineArrayList;
    private ProgressDialog progressDialog;

    private LatLng sourceLocation, destinationLocation;
    private List<Marker> driverMarkers = new ArrayList<>();

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_ride);

        binding.toolbarLayout.toolbar.setTitle("Book Ride");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        activePassengers = new ActivePassengers();

        geocoder = new Geocoder(this, Locale.getDefault());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        if (!Utils.isLocationEnabled(this)) {
            alertForNoGps();
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);


        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("PK")
                .build();
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));
                }
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(BookRideActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.selectLocationBtn.getText().toString().equalsIgnoreCase("Set Pick-Up")) {

                    try {
                        //Get source address from latLng
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        sourceLocation = latLng;
                        String addressLine = addresses.get(0).getAddressLine(0);

                        //Set source address and latlng in active driver
                        activePassengers.setPickup(addressLine);
                        activePassengers.setPickupLatLng(latLng.latitude + "," + latLng.longitude);


                        //Change button text to Set Destination
                        binding.selectLocationBtn.setText("Set Drop-Off");

                        //Place source marker
                        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
                        ImageView pin_image = marker.findViewById(R.id.pin_img);
                        pin_image.setImageDrawable(getResources().getDrawable(R.drawable.source_pin));
                        TextView marker_text = marker.findViewById(R.id.marker_text);
                        marker_text.setText("Pickup");

                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(BookRideActivity.this, marker))));


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else if (binding.selectLocationBtn.getText().toString().equalsIgnoreCase("Set Drop-Off")) {

                    try {
                        //Get source address from latLng
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        destinationLocation = latLng;
                        String addressLine = addresses.get(0).getAddressLine(0);

                        //Set source address and latlng in active driver
                        activePassengers.setDropoff(addressLine);
                        activePassengers.setDropoffLatLng(latLng.latitude + "," + latLng.longitude);


                        binding.selectLocationBtn.setText("Let's Go");


                        //Place source marker
                        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
                        ImageView pin_image = marker.findViewById(R.id.pin_img);
                        pin_image.setImageDrawable(getResources().getDrawable(R.drawable.destination_pin));
                        TextView marker_text = marker.findViewById(R.id.marker_text);
                        marker_text.setText("Dropoff");

                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(BookRideActivity.this, marker))));

                        adjustBounds();
                        drawRoute(activePassengers.getPickupLatLng(), activePassengers.getDropoffLatLng());
                        binding.confirmAddressMapCustomMarker.setVisibility(GONE);
                        binding.idCardView.setVisibility(GONE);
                        binding.clearBtn.setVisibility(View.VISIBLE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (binding.selectLocationBtn.getText().toString().equalsIgnoreCase("Search for Driver")) {
                    getActiveDrivers();
                } else {
                    //Lets Go
                    setActivePassenger();
                }

            }
        });

        binding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.clearBtn.setVisibility(GONE);
                binding.idCardView.setVisibility(View.VISIBLE);
                binding.selectLocationBtn.setText("Set Pick-Up");
                binding.confirmAddressMapCustomMarker.setVisibility(View.VISIBLE);
                mMap.clear();
                latLng = new LatLng(24.8754092, 67.0387518);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }
        });


    }

    private void setActivePassenger() {

        if (activePassengers != null) {

            progressDialog.show();

            activePassengers.setUserId(user.getUserId());


            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).removeEventListener(valueEventListener);

                    passenger_id = dataSnapshot.getChildrenCount();
                    passenger_id++;

                    Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).child(String.valueOf(passenger_id)).
                            setValue(activePassengers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "Child added");
                            binding.clearBtn.setVisibility(GONE);
                            binding.selectLocationBtn.setText("Search for Driver");
                            getActiveDrivers();
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
                    Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                }
            };
            Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).addListenerForSingleValueEvent(valueEventListener);

        }
    }

    private void getActiveDrivers() {
        try {

            progressDialog.setMessage("Searching for drivers...");
            if (!progressDialog.isShowing())
                progressDialog.show();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                    Log.e(TAG, "Active Drivers = " + dataSnapshot.getChildrenCount() + "");

                    List<ActiveDrivers> activeDrivers = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ActiveDrivers driver = data.getValue(ActiveDrivers.class);
                        activeDrivers.add(driver);
                    }

                    if (activeDrivers.size() == 0) {

                        if (driverMarkers.size() > 0) {
                            for (Marker marker : driverMarkers) {
                                marker.remove();
                            }

                            driverMarkers.clear();
                        }

                        Toast.makeText(BookRideActivity.this, "No Driver Found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showDrivers(activeDrivers);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                }
            };
            Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).addListenerForSingleValueEvent(valueEventListener);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDrivers(List<ActiveDrivers> activeDrivers) {

        for (Marker marker : driverMarkers) {
            marker.remove();
        }

        driverMarkers.clear();

        for (ActiveDrivers activeDriver : activeDrivers) {
            String[] latLngString = activeDriver.getCurrentLatlng().split(",");
            LatLng driverPosition = new LatLng(Double.parseDouble(latLngString[0]), Double.parseDouble(latLngString[1]));
            View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
            driverMarkers.add(mMap.addMarker(new MarkerOptions()
                    .position(driverPosition)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(BookRideActivity.this, marker)))));

        }

        adjustBounds(driverMarkers);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.e(TAG, "onMapReady");

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        latLng = new LatLng(24.8754092, 67.0387518);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                DecimalFormat decimalFormat = new DecimalFormat("##.#######");
                double latitude = Double.parseDouble(decimalFormat.format(mMap.getCameraPosition().target.latitude));
                double longitude = Double.parseDouble(decimalFormat.format(mMap.getCameraPosition().target.longitude));
                latLng = new LatLng(latitude, longitude);

                Log.e(TAG, "Moving latlng = " + latLng.latitude + "," + latLng.longitude);
            }
        });

        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 250);
        }

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

    @Override
    public void onConnected(Bundle bundle) {

        Log.e(TAG, "onConnected");
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if (passenger_id != 0) {
            Firebase.getInstance().mDatabase.child(ACTIVE_PASSENGERS).child(String.valueOf(passenger_id)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                }
            });
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
            activePassengers.setCurrentLatlng(myLatLng.latitude + "," + myLatLng.longitude);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onBackPressed() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Confirm");
        builder.setMessage("Are you sure you want to discard this booking?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
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
                DataParser parser = new DataParser(BookRideActivity.this);

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
