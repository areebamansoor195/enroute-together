package com.example.areebamansoor.enroutetogether.fragments;

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

import com.example.areebamansoor.enroutetogether.PassengerActivity;
import com.example.areebamansoor.enroutetogether.R;
import com.example.areebamansoor.enroutetogether.adapters.BookRidesAdapter;
import com.example.areebamansoor.enroutetogether.databinding.FragmentBookRidesBinding;
import com.example.areebamansoor.enroutetogether.model.ActivePassengers;
import com.example.areebamansoor.enroutetogether.model.User;
import com.example.areebamansoor.enroutetogether.utils.SharedPreferencHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_DRIVERS;
import static com.example.areebamansoor.enroutetogether.utils.Constants.ACTIVE_PASSENGERS;

public class FragmentBookRides extends Fragment {

    private FragmentBookRidesBinding binding;
    private BookRidesAdapter adapter;
    private List<ActivePassengers> activePassengersList = new ArrayList<>();
    private ValueEventListener valueEventListener;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new Gson().fromJson(SharedPreferencHandler.getUser(), User.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_rides, container, false);


        adapter = new BookRidesAdapter(this, activePassengersList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.bookRidesList.setLayoutManager(mLayoutManager);
        binding.bookRidesList.setItemAnimator(new DefaultItemAnimator());
        binding.bookRidesList.setAdapter(adapter);

        getMyBookRides();

        return binding.getRoot();
    }

    public void onClickItem(final ActivePassengers activePassengers) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to?")
                .setCancelable(false)
                .setPositiveButton("Show", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(getActivity(), PassengerActivity.class);
                        intent.putExtra("Job", new Gson().toJson(activePassengers));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        /*if (activePassengers.getRequestedDriver() != null)
                            deleteRideFromDriver(activePassengers);
*/
                        final DatabaseReference currentRideRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS).child(activePassengers.getUserId());
                        valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentRideRef.removeEventListener(valueEventListener);
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    ActivePassengers mCurrentRide = data.getValue(ActivePassengers.class);
                                    if (mCurrentRide != null) {
                                        if (mCurrentRide.getTimeStamp().equalsIgnoreCase(activePassengers.getTimeStamp()))
                                            currentRideRef.child(data.getKey()).removeValue();
                                    }
                                }

                                SharedPreferencHandler.setHasPendingBookRide(false);
                                activePassengersList.remove(activePassengers);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                currentRideRef.removeEventListener(valueEventListener);
                            }
                        };
                        currentRideRef.limitToLast(1).addListenerForSingleValueEvent(valueEventListener);
                        dialog.dismiss();
                        getMyBookRides();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteRideFromDriver(final ActivePassengers activePassengers) {

       /* final DatabaseReference activeDriverRef = FirebaseDatabase.getInstance().getReference(ACTIVE_DRIVERS).child(activePassengers.getRequestedDriver());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                activeDriverRef.removeEventListener(valueEventListener);

                List<ActiveDrivers> driverOfferRides = new ArrayList<>();

                for (DataSnapshot offerRides : dataSnapshot.getChildren()) {
                    ActiveDrivers ride = offerRides.getValue(ActiveDrivers.class);
                    driverOfferRides.add(ride);
                }

                String[] passengerRequests = driverOfferRides.get(0).getPassengerRequests().split(",");

                if (passengerRequests.length == 1) {
                    driverOfferRides.get(0).setPassengerRequests(null);
                } else {
                    driverOfferRides.get(0).setPassengerRequests("");
                    for (int i = 0; i < passengerRequests.length; i++) {
                        if (!passengerRequests[i].equalsIgnoreCase(user.getUserId())) {
                            if (i == 0) {
                                driverOfferRides.get(0).setPassengerRequests(passengerRequests[i]);
                            } else {
                                driverOfferRides.get(0).setPassengerRequests("," + passengerRequests[i]);
                            }
                        }
                    }
                }

                Firebase.getInstance().mDatabase.child(ACTIVE_DRIVERS).child(activePassengers.getRequestedDriver()).
                        setValue(driverOfferRides).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activeDriverRef.removeEventListener(valueEventListener);
            }
        };
        activeDriverRef.addListenerForSingleValueEvent(valueEventListener);*/
    }

    public void getMyBookRides() {

        final DatabaseReference activePassengerRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS).child(user.getUserId());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                activePassengerRef.removeEventListener(valueEventListener);

                binding.progress.setVisibility(GONE);

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ActivePassengers activePassengers = data.getValue(ActivePassengers.class);
                    activePassengersList.add(activePassengers);
                }

                if (activePassengersList.size() > 0) {

                    adapter = new BookRidesAdapter(FragmentBookRides.this, activePassengersList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    binding.bookRidesList.setLayoutManager(mLayoutManager);
                    binding.bookRidesList.setItemAnimator(new DefaultItemAnimator());
                    binding.bookRidesList.setAdapter(adapter);

                } else {
                    binding.noResultFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activePassengerRef.child(ACTIVE_DRIVERS).removeEventListener(valueEventListener);
            }
        };
        activePassengerRef.addValueEventListener(valueEventListener);
    }
}
