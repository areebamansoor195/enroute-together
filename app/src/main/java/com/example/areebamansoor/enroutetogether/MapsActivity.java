package com.example.areebamansoor.enroutetogether;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;

    private ImageView mGps;

    private Button btnConfirm;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public String check = "default";
//    PlaceAutocompleteFragment placeAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGps = (ImageView) findViewById(R.id.ic_gps);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        etOrigin = (EditText) findViewById(R.id.source);
        etDestination = (EditText) findViewById(R.id.dest);

        Intent intent = getIntent();
        check = intent.getStringExtra("map");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check.equals("offer_ride")){
                    Intent intent = new Intent( MapsActivity.this,Offer_OfferRide.class);
                    intent.putExtra("to", etDestination.getText().toString());
                    intent.putExtra("from", etOrigin.getText().toString());
                    startActivity(intent);
                }
                if(check.equals("book_ride")){
                    Intent intent = new Intent( MapsActivity.this,Book_DriversList.class);
                    startActivity(intent);
                }
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
    }

//    public void onClick(View view){
//        if(view == mGps){
//            getDeviceLocation();
//        }
//        else{
//            Toast.makeText(MapsActivity.this, "onclick else ", Toast.LENGTH_SHORT).show();
//
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

//            initSource();
//            initDestination();
        }

    }

    private void getLocationPermission() {
//        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    String address;

    private void geoLocate(int decider) {

        if (decider == 8) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            try {
                if (mLocationPermissionsGranted) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
//                                Log.d(TAG, "onComplete: found location!");
                                Location currentLocation = (Location) task.getResult();
                                Geocoder geocoder = new Geocoder(MapsActivity.this);
                                List<Address> listSource = new ArrayList<Address>();

                                double lat = currentLocation.getLatitude();
                                double lon = currentLocation.getLongitude();

//                                try {
//                                    listSource = geocoder.getFromLocation(lat, lon, 1);
//                                    address = listSource.get(0).getAddressLine(0);
//                                    mSourceSearchText.setText(address);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }
                    });
                }

            }       catch (Exception e){

            }
        }
        //            try{
//                listSource = geocoder.getFromLocation(, 1);
//            }catch (IOException e){
//                Log.e(TAG, "geoLocate Source: IOException: " + e.getMessage() );
//            }




//
//            Log.d(TAG, "geoLocate Source: geolocating Source");
//
//            Geocoder geocoder = new Geocoder(MapNavigation.this);
//            List<Address> listSource = new ArrayList<>();
//            try{
//                listSource = geocoder.getFromLocation(, 1);
//            }catch (IOException e){
//                Log.e(TAG, "geoLocate Source: IOException: " + e.getMessage() );
//            }
//
//            if(listSource.size() > 0){
//                Address address = listSource.get(0);
//
//                Log.d(TAG, "geoLocate: found a location: " + address.toString());
//                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//
//                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                        address.getAddressLine(0));
//            }



//        if(decider == 9)
//        {
//            Log.d(TAG, "geoLocate Source: geolocating Source");
//            String searchSourceString = mSourceSearchText.getText().toString();
//
//            Geocoder geocoder = new Geocoder(MapNavigation.this);
//            List<Address> listSource = new ArrayList<>();
//            try{
//                listSource = geocoder.getFromLocationName(searchSourceString, 1);
//            }catch (IOException e){
//                Log.e(TAG, "geoLocate Source: IOException: " + e.getMessage() );
//            }
//
//            if(listSource.size() > 0){
//                Address address = listSource.get(0);
//
//                Log.d(TAG, "geoLocate: found a location: " + address.toString());
//                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//
//                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                        address.getAddressLine(0));
//            }
//        }
//
//        if(decider == 10)
//        {
//            Log.d(TAG, "geoLocate Destination: geolocating Destination");
//
//            String searchDestString = mDestSearchText.getText().toString();
//
//            Geocoder geocoder = new Geocoder(MapNavigation.this);
//            List<Address> listDest = new ArrayList<>();
//            try{
//                listDest = geocoder.getFromLocationName(searchDestString, 1);
//            }catch (IOException e){
//                Log.e(TAG, "geoLocate Destination: IOException: " + e.getMessage() );
//            }
//
//            if(listDest.size() > 0){
//                Address address = listDest.get(0);
//
//                Log.d(TAG, "geoLocate Destination: found a location: " + address.toString());
//                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//
//                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                        address.getAddressLine(0));
//            }
//        }

    }

    private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        Toast.makeText(this, "IN getDeviceLocation ", Toast.LENGTH_SHORT).show();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                Toast.makeText(this, "IN getDeviceLocation ------------>  if(mLocationPermissionsGranted)", Toast.LENGTH_SHORT).show();

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete( Task task) {
                        if(task.isSuccessful()){
//                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");
                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
//            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
//        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        /*if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }*/

        hideSoftKeyboard();
    }

    private void initMap(){
//        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}