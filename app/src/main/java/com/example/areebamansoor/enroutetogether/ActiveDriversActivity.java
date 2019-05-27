package com.example.areebamansoor.enroutetogether;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.areebamansoor.enroutetogether.adapters.OfferRidesAdapter;
import com.example.areebamansoor.enroutetogether.databinding.ActivityActiveDriversBinding;
import com.example.areebamansoor.enroutetogether.firebase.Firebase;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;

public class ActiveDriversActivity extends AppCompatActivity {

    private ActivityActiveDriversBinding binding;
    private OfferRidesAdapter adapter;

    private ProgressDialog progressDialog;
    private List<ActiveDrivers> activeDriversList = new ArrayList<>();
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_active_drivers);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        binding.toolbarLayout.toolbar.setTitle("Active Drivers");

        adapter = new OfferRidesAdapter(activeDriversList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.activeDriversList.setLayoutManager(mLayoutManager);
        binding.activeDriversList.setItemAnimator(new DefaultItemAnimator());
        binding.activeDriversList.setAdapter(adapter);


        getActiveDrivers();
    }

    private void getActiveDrivers() {

        progressDialog.show();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).removeEventListener(valueEventListener);
                progressDialog.dismiss();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ActiveDrivers activeDrivers = data.getValue(ActiveDrivers.class);
                    activeDriversList.add(activeDrivers);
                }

                if (activeDriversList.size() > 0) {

                    adapter = new OfferRidesAdapter(activeDriversList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    binding.activeDriversList.setLayoutManager(mLayoutManager);
                    binding.activeDriversList.setItemAnimator(new DefaultItemAnimator());
                    binding.activeDriversList.setAdapter(adapter);

                } else {
                    binding.noDriverFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).removeEventListener(valueEventListener);
            }
        };
        Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).addValueEventListener(valueEventListener);
    }
}
