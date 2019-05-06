package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
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
        final String model = binding.etModel.getText().toString().trim();
        final String color = binding.etColor.getText().toString().trim();
        final String capacity = binding.etCapacity.getText().toString().trim();
        final String plateNumber = binding.etNumberPlate.getText().toString().trim();

        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase.getInstance().mDatabase.child(VEHICLE).removeEventListener(valueEventListener);

                vehicle_id = dataSnapshot.getChildrenCount();
                vehicle_id++;

                user.setVehicleId(String.valueOf(vehicle_id));
                Firebase.getInstance().mDatabase.child(USERS).child(user.getUserId()).setValue(user);

                final Vehicle vehicle = new Vehicle(makers, model, color, Integer.parseInt(capacity), String.valueOf(vehicle_id), plateNumber);

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Firebase.getInstance().mDatabase.child(VEHICLE).child(vehicle_id + "").removeEventListener(valueEventListener);
                        progressDialog.dismiss();
                        SharedPreferencHandler.setVehicle(new Gson().toJson(vehicle));
                        Toast.makeText(AddVehicleActivity.this, "Vehicle add successfully", Toast.LENGTH_SHORT).show();
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
        String model = binding.etModel.getText().toString().trim();
        String color = binding.etColor.getText().toString().trim();
        String capacity = binding.etCapacity.getText().toString().trim();
        String plateNumber = binding.etCapacity.getText().toString().trim();

        if (TextUtils.isEmpty(makers) || TextUtils.isEmpty(model) ||
                TextUtils.isEmpty(color) || TextUtils.isEmpty(capacity)
                || TextUtils.isEmpty(plateNumber)) {
            return false;
        }

        return true;
    }
}
