package com.juw.areebamansoor.enroutetogether.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juw.areebamansoor.enroutetogether.DriverActivity;
import com.juw.areebamansoor.enroutetogether.R;
import com.juw.areebamansoor.enroutetogether.adapters.OfferRidesAdapter;
import com.juw.areebamansoor.enroutetogether.databinding.FragmentOfferRidesBinding;
import com.juw.areebamansoor.enroutetogether.model.ActiveDrivers;
import com.juw.areebamansoor.enroutetogether.model.User;
import com.juw.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.juw.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;

public class FragmentOfferRides extends Fragment {

    private FragmentOfferRidesBinding binding;
    private OfferRidesAdapter adapter;
    private List<ActiveDrivers> activeDriversList = new ArrayList<>();
    private ValueEventListener valueEventListener;
    private User user;

    private DatabaseReference activeDriverRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offer_rides, container, false);


        adapter = new OfferRidesAdapter(this, activeDriversList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.offerRidesList.setLayoutManager(mLayoutManager);
        binding.offerRidesList.setItemAnimator(new DefaultItemAnimator());
        binding.offerRidesList.setAdapter(adapter);

        getMyOfferRides();

        return binding.getRoot();
    }

    public void onClickItem(final ActiveDrivers activeDrivers) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to?")
                .setPositiveButton("Show", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(getActivity(), DriverActivity.class);
                        intent.putExtra("Job", new Gson().toJson(activeDrivers));
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final DatabaseReference currentRideRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(activeDrivers.getDriverDetails().getUserId());
                        valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentRideRef.removeEventListener(valueEventListener);
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    ActiveDrivers mCurrentRide = data.getValue(ActiveDrivers.class);
                                    if (mCurrentRide != null) {
                                        if (mCurrentRide.getTimeStamp().equalsIgnoreCase(activeDrivers.getTimeStamp()))
                                            currentRideRef.child(data.getKey()).removeValue();
                                    }
                                }

                                SharedPreferencHandler.setHasPendingOfferRide(false);
                                activeDriversList.remove(activeDrivers);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                currentRideRef.removeEventListener(valueEventListener);
                            }
                        };
                        currentRideRef.limitToLast(1).addListenerForSingleValueEvent(valueEventListener);
                        dialog.dismiss();
                        getMyOfferRides();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getMyOfferRides() {

        activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(user.getUserId());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                binding.progress.setVisibility(GONE);
                activeDriversList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ActiveDrivers activeDrivers = data.getValue(ActiveDrivers.class);

                    activeDriversList.add(activeDrivers);
                }

                if (activeDriversList.size() > 0) {

                    adapter = new OfferRidesAdapter(FragmentOfferRides.this, activeDriversList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    binding.offerRidesList.setLayoutManager(mLayoutManager);
                    binding.offerRidesList.setItemAnimator(new DefaultItemAnimator());
                    binding.offerRidesList.setAdapter(adapter);

                } else {
                    binding.noResultFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        activeDriverRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activeDriverRef.removeEventListener(valueEventListener);
    }
}
