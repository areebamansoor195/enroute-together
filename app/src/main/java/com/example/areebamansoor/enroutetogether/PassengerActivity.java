package com.example.areebamansoor.enroutetogether;

import android.Manifest;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityPassengerBinding;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.example.areebamansoor.enroutetogether.model.ActivePassengers;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.utils.DataParser;
import com.example.areebamansoor.enroutetogether.utils.MapAnimator;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.example.areebamansoor.enroutetogether.utils.Utils;
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

import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_PASSENGERS;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "DriverActivity";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private ActivityPassengerBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ArrayList<Polyline> polylineArrayList;
    private LatLng sourceLocation, destinationLocation;
    private User user;
    private LatLng myLatLng;
    private ActivePassengers myBookRide;

    private DatabaseReference activePassengerRef;
    private DatabaseReference activeDriverRef;
    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListener2;
    private Marker driverMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_passenger);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        if (!Utils.isLocationEnabled(this)) {
            alertForNoGps();
        }

        binding.toolbarLayout.toolbar.setTitle("My Book Ride");

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);

        myBookRide = new Gson().fromJson(getIntent().getStringExtra("Job"), ActivePassengers.class);
        String[] sourceLatLngStr = myBookRide.getPickupLatLng().split(",");
        String[] destLatLngStr = myBookRide.getDropoffLatLng().split(",");

        sourceLocation = new LatLng(Double.parseDouble(sourceLatLngStr[0]), Double.parseDouble(sourceLatLngStr[1]));
        destinationLocation = new LatLng(Double.parseDouble(destLatLngStr[0]), Double.parseDouble(destLatLngStr[1]));


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activePassengerRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS).child(user.getUserId());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActivePassengers activePassengers = null;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    activePassengers = data.getValue(ActivePassengers.class);
                }
                myBookRide = activePassengers;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        activePassengerRef.addValueEventListener(valueEventListener);

        activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(myBookRide.getRequestedDriver());

        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ActiveDrivers activeDrivers = null;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    activeDrivers = data.getValue(ActiveDrivers.class);
                }
                if (myBookRide.getRideAccepted()) {
                    drawAcceptedDriver(activeDrivers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        activeDriverRef.addValueEventListener(valueEventListener2);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void drawAcceptedDriver(ActiveDrivers activeDriver) {

        String initialLatLng = activeDriver.getCurrentLatlng();
        String[] latLngString = initialLatLng.split(",");
        LatLng driverPosition = new LatLng(Double.parseDouble(latLngString[0]), Double.parseDouble(latLngString[1]));
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        if (activeDriver.getDriverDetails().getImage_url() != null) {
            CircleImageView profile_img = marker.findViewById(R.id.profileImage);
            Log.e(TAG, activeDriver.getDriverDetails().getImage_url());
            Picasso.get()
                    .load(activeDriver.getDriverDetails().getImage_url())
                    .placeholder(R.drawable.icon2)
                    .into(profile_img);
        }

        if (driverMarker == null)
            driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(driverPosition)
                    .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(PassengerActivity.this, marker))));
        else
            driverMarker.setPosition(driverPosition);

        adjustBounds(driverMarker);
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

    private void adjustBounds(Marker driverPosition) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (sourceLocation != null)
            builder.include(sourceLocation);

        if (destinationLocation != null)
            builder.include(destinationLocation);

        if (driverPosition != null) {
            builder.include(driverPosition.getPosition());
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
            myBookRide.setCurrentLatlng(myLatLng.latitude + "," + myLatLng.longitude);

            /*if (myBookRide.getRideAccepted())
                drawAcceptedDriver();*/
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
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activePassengerRef.removeEventListener(valueEventListener);
        activeDriverRef.removeEventListener(valueEventListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 13));

        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
        ImageView pin_image = marker.findViewById(R.id.pin_img);
        pin_image.setImageDrawable(getResources().getDrawable(R.drawable.source_pin));
        TextView marker_text = marker.findViewById(R.id.marker_text);
        marker_text.setText("Source");

        mMap.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(PassengerActivity.this, marker))));

        View Destmarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_text_layout, null);
        ImageView pin_image_d = Destmarker.findViewById(R.id.pin_img);
        pin_image_d.setImageDrawable(getResources().getDrawable(R.drawable.destination_pin));
        TextView marker_text_d = Destmarker.findViewById(R.id.marker_text);
        marker_text_d.setText("Dest");

        mMap.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.createDrawableFromView(PassengerActivity.this, Destmarker))));

        drawRoute(myBookRide.getPickupLatLng(), myBookRide.getDropoffLatLng());

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
                DataParser parser = new DataParser(PassengerActivity.this);

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