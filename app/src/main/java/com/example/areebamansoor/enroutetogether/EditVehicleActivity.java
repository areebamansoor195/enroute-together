package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityEditVehicleBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.Vehicle;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class EditVehicleActivity extends AppCompatActivity {

    private ActivityEditVehicleBinding binding;
    private Vehicle vehicle;
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_vehicle);

        binding.toolbarLayout.toolbar.setTitle("Edit Vehicle");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");


        vehicle = new Gson().fromJson(SharedPreferencHandler.getVehicle(), Vehicle.class);


        binding.etMakersName.setText(vehicle.getMaker());
        binding.etModel.setText(vehicle.getModel());
        binding.etCapacity.setText(vehicle.getCapacity() + "");
        binding.etColor.setText(vehicle.getColor());
        binding.etNumberPlate.setText(vehicle.getPlateNumber());


        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    editVehicle();
                    return;
                }


                Toast.makeText(EditVehicleActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void editVehicle() {

        vehicle.setMaker(binding.etMakersName.getText().toString().trim());
        vehicle.setModel(binding.etModel.getText().toString().trim());
        vehicle.setColor(binding.etColor.getText().toString().trim());
        vehicle.setCapacity(Integer.parseInt(binding.etCapacity.getText().toString().trim()));
        vehicle.setPlateNumber(binding.etNumberPlate.getText().toString().trim());


        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase.getInstance().mDatabase.child("Vehicle").child(vehicle.getVehicleId()).removeEventListener(valueEventListener);
                progressDialog.dismiss();
                SharedPreferencHandler.setVehicle(new Gson().toJson(vehicle));
                finish();
                Toast.makeText(EditVehicleActivity.this, "Vehicle updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Firebase.getInstance().mDatabase.child("Vehicle").child(vehicle.getVehicleId()).removeEventListener(valueEventListener);
                progressDialog.dismiss();
            }
        };
        Firebase.getInstance().mDatabase.child("Vehicle").child(vehicle.getVehicleId()).setValue(vehicle);
        Firebase.getInstance().mDatabase.child("Vehicle").child(vehicle.getVehicleId()).addValueEventListener(valueEventListener);

    }

    private boolean validateFields() {
        String makers = binding.etMakersName.getText().toString().trim();
        String model = binding.etModel.getText().toString().trim();
        String color = binding.etColor.getText().toString().trim();
        String capacity = binding.etCapacity.getText().toString().trim();
        String plateNumber = binding.etNumberPlate.getText().toString().trim();

        if (TextUtils.isEmpty(makers) || TextUtils.isEmpty(model) ||
                TextUtils.isEmpty(color) || TextUtils.isEmpty(capacity) || TextUtils.isEmpty(plateNumber)) {
            return false;
        }

        return true;
    }
}
