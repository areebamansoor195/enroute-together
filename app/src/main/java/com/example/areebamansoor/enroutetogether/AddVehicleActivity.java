package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityAddVehicleBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.model.Vehicle;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.util.Arrays;

import static com.example.areebamansoor.enroutetogether.utils.Constants.USERS;
import static com.example.areebamansoor.enroutetogether.utils.Constants.VEHICLE;

public class AddVehicleActivity extends AppCompatActivity {

    private ActivityAddVehicleBinding binding;
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListener;
    private long vehicle_id = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_vehicle);
        binding.toolbarLayout.toolbar.setTitle("Add Vehicle");

        binding.type.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.vehicle_types))));
        binding.color.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.vehicle_colors))));
        binding.model.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.vehicle_models))));
        binding.capacity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.vehicle_capacity))));

        binding.type.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {

            }
        });
        binding.color.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {

            }
        });
        binding.model.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {

            }
        });
        binding.capacity.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {

            }
        });
        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateFields()) {
                    createVehicle();
                    return;
                }

                Toast.makeText(AddVehicleActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createVehicle() {

        final String makers = binding.etMakersName.getText().toString().trim();
        final String model = binding.model.getSelectedItem();
        final String color = binding.color.getSelectedItem();
        final String capacity = binding.capacity.getSelectedItem();
        final String plateNumber = binding.etNumberPlate.getText().toString().trim();
        final String type = binding.type.getSelectedItem();

        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase.getInstance().mDatabase.child(VEHICLE).removeEventListener(valueEventListener);

                vehicle_id = dataSnapshot.getChildrenCount();
                vehicle_id++;

                user.setVehicleId(String.valueOf(vehicle_id));
                Firebase.getInstance().mDatabase.child(USERS).child(user.getUserId()).setValue(user);

                final Vehicle vehicle = new Vehicle(makers, model, color, Integer.parseInt(capacity), String.valueOf(vehicle_id), plateNumber, type);

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Firebase.getInstance().mDatabase.child(VEHICLE).child(vehicle_id + "").removeEventListener(valueEventListener);
                        progressDialog.dismiss();
                        SharedPreferencHandler.setVehicle(new Gson().toJson(vehicle));
                        SharedPreferencHandler.setUser(new Gson().toJson(user));
                        Toast.makeText(AddVehicleActivity.this, "Vehicle add successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddVehicleActivity.this, OfferRideActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Firebase.getInstance().mDatabase.child(VEHICLE).child(vehicle_id + "").removeEventListener(valueEventListener);
                    }
                };
                Firebase.getInstance().mDatabase.child(VEHICLE).child(vehicle_id + "").setValue(vehicle);
                Firebase.getInstance().mDatabase.child(VEHICLE).child(vehicle_id + "").addValueEventListener(valueEventListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Firebase.getInstance().mDatabase.child(VEHICLE).removeEventListener(valueEventListener);
            }
        };
        Firebase.getInstance().mDatabase.child(VEHICLE).addListenerForSingleValueEvent(valueEventListener);

    }

    private boolean validateFields() {
        String makers = binding.etMakersName.getText().toString().trim();
        String model = binding.model.getSelectedItem();
        String color = binding.color.getSelectedItem();
        String capacity = binding.capacity.getSelectedItem();
        String plateNumber = binding.etNumberPlate.getText().toString().trim();
        String type = binding.type.getSelectedItem();

        if (TextUtils.isEmpty(makers) || TextUtils.isEmpty(model) ||
                TextUtils.isEmpty(color) || TextUtils.isEmpty(capacity)
                || TextUtils.isEmpty(plateNumber) || TextUtils.isEmpty(type)) {
            return false;
        }

        return true;
    }
}
