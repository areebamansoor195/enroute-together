package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.areebamansoor.enroutetogether.databinding.ActivityEditVehicleBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.Vehicle;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.util.Arrays;

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


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");


        vehicle = new Gson().fromJson(SharedPreferencHandler.getVehicle(), Vehicle.class);
        binding.etMakersName.setText(vehicle.getName());
        binding.etNumberPlate.setText(vehicle.getPlateNumber());
        binding.model.setSelection(Arrays.asList(getResources().getStringArray(R.array.vehicle_models)).indexOf(vehicle.getModel()));
        binding.capacity.setSelection(Arrays.asList(getResources().getStringArray(R.array.vehicle_capacity)).indexOf(vehicle.getCapacity()));
        binding.type.setSelection(Arrays.asList(getResources().getStringArray(R.array.vehicle_types)).indexOf(vehicle.getType()));
        binding.color.setSelection(Arrays.asList(getResources().getStringArray(R.array.vehicle_colors)).indexOf(vehicle.getColor()));


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

        vehicle.setName(binding.etMakersName.getText().toString().trim());
        vehicle.setModel(binding.model.getSelectedItem());
        vehicle.setColor(binding.color.getSelectedItem());
        vehicle.setType(binding.type.getSelectedItem());
        vehicle.setCapacity(Integer.parseInt(binding.capacity.getSelectedItem()));
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
        String model = binding.model.getSelectedItem();
        String color = binding.color.getSelectedItem();
        String type = binding.type.getSelectedItem();
        String capacity = binding.capacity.getSelectedItem();
        String plateNumber = binding.etNumberPlate.getText().toString().trim();

        if (TextUtils.isEmpty(makers) || TextUtils.isEmpty(model) ||
                TextUtils.isEmpty(color) || TextUtils.isEmpty(capacity) || TextUtils.isEmpty(plateNumber)
                || TextUtils.isEmpty(type)) {
            return false;
        }

        return true;
    }
}
