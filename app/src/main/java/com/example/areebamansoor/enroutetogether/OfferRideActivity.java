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
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityOfferRideBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.model.Vehicle;
import com.example.areebamansoor.enroutetogether.utils.DataParser;
import com.example.areebamansoor.enroutetogether.utils.MapAnimator;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.example.areebamansoor.enroutetogether.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.model.Place;

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
import java.util.Locale;

import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVER;
import static com.example.areebamansoor.enroutetogether.utils.Constants.OFFLINE;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ONLINE;

public class OfferRideActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "OfferRideActivity";
    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    private ActivityOfferRideBinding binding;
    private GoogleMap mMap;
    private LatLng myLatLng;
    private User user;
    private Vehicle vehicle;
    private ActiveDrivers activeDrivers;
    private ValueEventListener valueEventListener;
    private SupportMapFragment mapFragment;
    private Geocoder geocoder;
    private List<Address> addresses;

    private ArrayList<Polyline> polylineArrayList;
    private ProgressDialog progressDialog;

    private LatLng sourceLocation, destinationLocation;

    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_offer_ride);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);
        activeDrivers = new ActiveDrivers();
        geocoder = new Geocoder(this, Locale.getDefault());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

<<<<<<< HEAD
        //setDriverActive();
=======
>>>>>>> e55d5716502300c82f5bef67b31ce40548aaec47

        if (!Utils.isLocationEnabled(this)) {
            alertForNoGps();
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OfferRideActivity.this, EditVehicleActivity.class));
            }
        });


        binding.etSource.setHistoryManager(null);
        binding.etDropoff.setHistoryManager(null);

        binding.etSource.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                activeDrivers.setSource(place.description);
                getPlaceLatLng("pickup", place);
            }
        });

        binding.etDropoff.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                activeDrivers.setDestination(place.description);
                getPlaceLatLng("dropoff", place);
            }
        });


        binding.letsStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etSource.getHint().toString().equalsIgnoreCase("Pick up")) {
                    Toast.makeText(OfferRideActivity.this, "Please enter pick up", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.etDropoff.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(OfferRideActivity.this, "Please enter drop off", Toast.LENGTH_SHORT).show();
                    return;
                }

                setActiveDriver();

            }
        });

        binding.activeBusyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeDrivers.getStatus().equalsIgnoreCase(OFFLINE)) {
                    updateDriverStatus(ONLINE);
                } else {
                    updateDriverStatus(OFFLINE);
                }
            }
        });
    }

    private void updateDriverStatus(final String status) {

        progressDialog.show();

        if (status.equalsIgnoreCase(OFFLINE)) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Firebase.getInstance().mDatabase.removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Firebase.getInstance().mDatabase.removeEventListener(valueEventListener);
                    progressDialog.dismiss();

                }
            };
            Firebase.getInstance().mDatabase.addValueEventListener(valueEventListener);
            Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(activeDrivers.getId()).removeValue();
            return;
        }

        activeDrivers.setStatus(status);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase.getInstance().mDatabase.removeEventListener(valueEventListener);

                progressDialog.dismiss();

                binding.activeBusyBtn.setText("Go " + OFFLINE);
                binding.activeBusyBtn.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Firebase.getInstance().mDatabase.removeEventListener(valueEventListener);
                progressDialog.dismiss();
            }
        };
        Firebase.getInstance().mDatabase.addValueEventListener(valueEventListener);
        Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(activeDrivers.getId()).setValue(activeDrivers);
    }

    private void getPlaceLatLng(final String from, Place place) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.place_id).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (places.getStatus().isSuccess()) {
                    final com.google.android.gms.location.places.Place myPlace = places.get(0);
                    LatLng queriedLocation = myPlace.getLatLng();

                    Log.v("Latitude is", "" + queriedLocation.latitude);
                    Log.v("Longitude is", "" + queriedLocation.longitude);


                    if (from.equalsIgnoreCase("pickup")) {
                        activeDrivers.setSourceLatLng(queriedLocation.latitude + "," + queriedLocation.longitude);
                        sourceLocation = queriedLocation;
                    } else {
                        activeDrivers.setDestinationLatLng(queriedLocation.latitude + "," + queriedLocation.longitude);
                        destinationLocation = queriedLocation;
                    }

                }
                places.release();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

<<<<<<< HEAD
            private void setDriverActive() {
=======
        vehicle = new Gson().fromJson(SharedPreferencHandler.getVehicle(), Vehicle.class);
        binding.carMake.setText("Make : " + vehicle.getMaker());
        binding.carModel.setText("Model : " + vehicle.getModel());
        binding.carColor.setText("Color : " + vehicle.getColor());
        binding.carCapacity.setText("Capacity : " + vehicle.getCapacity());
        binding.carPlateNumber.setText("Plate Number : " + vehicle.getPlateNumber());
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

    private void setActiveDriver() {
>>>>>>> e55d5716502300c82f5bef67b31ce40548aaec47

                if (activeDrivers != null) {

            progressDialog.show();

                    valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).removeEventListener(valueEventListener);
                    activeDrivers.setId(user.getUserId());
                    activeDrivers.setVehicleId(vehicle.getVehicleId());
                    activeDrivers.setStatus(OFFLINE);
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(user.getUserId()).setValue(activeDrivers);

                    binding.setupRideLayout.setVisibility(View.GONE);
                    binding.activeBusyBtn.setVisibility(View.VISIBLE);


                    showMarkers();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).removeEventListener(valueEventListener);
                    progressDialog.dismiss();
                }
            };

            Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).addListenerForSingleValueEvent(valueEventListener);

        }

    }

    private void showMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(sourceLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.createPin(50, 50, R.drawable.dot, this))));

            mMap.addMarker(new MarkerOptions()
                    .position(destinationLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.createPin(50, 50, R.drawable.dot_red, this))));

            adjustBounds();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawRoute(activeDrivers.getSourceLatLng(), activeDrivers.getDestinationLatLng());
                }
            }, 1000);
        }
    }

    private void adjustBounds() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (myLatLng != null)
            builder.include(myLatLng);

        if (sourceLocation != null)
            builder.include(sourceLocation);

        if (destinationLocation != null)
            builder.include(destinationLocation);

        LatLngBounds bounds = builder.build();


        // begin new code:
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.22); // offset from edges of the map 12% of screen

        CameraUpdate cu = null;
        if (sourceLocation != null || destinationLocation != null) {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        } else {
            cu = CameraUpdateFactory.newLatLngZoom(myLatLng, 13);
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

<<<<<<< HEAD
       Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(activeDrivers.getId()).removeValue();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
=======
        //Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(activeDrivers.getId()).removeValue();
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
>>>>>>> e55d5716502300c82f5bef67b31ce40548aaec47
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
            activeDrivers.setCurrentLatlng(myLatLng.latitude + "," + myLatLng.longitude);

            if (binding.etSource.getText().toString().equalsIgnoreCase("")) {

                addresses = geocoder.getFromLocation(myLatLng.latitude, myLatLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                sourceLocation = myLatLng;
                String addressLine = addresses.get(0).getAddressLine(0);
                binding.etSource.setHint(addressLine + "");

<<<<<<< HEAD
          if (activeDrivers.getId() != null) {
              activeDrivers.setCurrentLatlng(myLatLng.latitude + "," + myLatLng.longitude);
                Firebase.getInstance().mDatabase.child(ACTIVE_DRIVER).child(activeDrivers.getId()).setValue(activeDrivers);
           }
            CameraPosition cameraPosition = new CameraPosition.Builder()
=======
                activeDrivers.setSource(addressLine);
                activeDrivers.setSourceLatLng(myLatLng.latitude + "," + myLatLng.longitude);
            }


            if (currentMarker != null) {
                currentMarker.setPosition(myLatLng);
            } else {

                View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
                ImageView imageView = marker.findViewById(R.id.profileImage);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_avataar));

                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(myLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(this, marker))));

            }
            adjustBounds();
            /*CameraPosition cameraPosition = new CameraPosition.Builder()
>>>>>>> e55d5716502300c82f5bef67b31ce40548aaec47
                    .target(myLatLng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
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
        super.onBackPressed();
        finish();
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
                DataParser parser = new DataParser(OfferRideActivity.this);

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
