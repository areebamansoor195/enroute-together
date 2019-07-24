package com.example.areebamansoor.enroutetogether.fragments;

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

import com.example.areebamansoor.enroutetogether.R;
import com.example.areebamansoor.enroutetogether.adapters.OfferRidesAdapter;
import com.example.areebamansoor.enroutetogether.databinding.FragmentOfferRidesBinding;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
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


        adapter = new OfferRidesAdapter(activeDriversList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.offerRidesList.setLayoutManager(mLayoutManager);
        binding.offerRidesList.setItemAnimator(new DefaultItemAnimator());
        binding.offerRidesList.setAdapter(adapter);

        getMyOfferRides();

        return binding.getRoot();
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

                    adapter = new OfferRidesAdapter(activeDriversList);

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
    public void onDestroy() {
        super.onDestroy();
        activeDriverRef.removeEventListener(valueEventListener);
    }
}
