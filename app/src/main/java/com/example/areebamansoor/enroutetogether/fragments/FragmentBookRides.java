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
import com.example.areebamansoor.enroutetogether.adapters.BookRidesAdapter;
import com.example.areebamansoor.enroutetogether.adapters.OfferRidesAdapter;
import com.example.areebamansoor.enroutetogether.databinding.FragmentBookRidesBinding;
import com.example.areebamansoor.enroutetogether.model.ActiveDrivers;
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


        adapter = new BookRidesAdapter(activePassengersList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.bookRidesList.setLayoutManager(mLayoutManager);
        binding.bookRidesList.setItemAnimator(new DefaultItemAnimator());
        binding.bookRidesList.setAdapter(adapter);

        getMyBookRides();

        return binding.getRoot();
    }

    private void getMyBookRides() {

        final DatabaseReference activePassengerRef = FirebaseDatabase.getInstance().getReference(ACTIVE_PASSENGERS).child(user.getUserId());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                activePassengerRef.removeEventListener(valueEventListener);

                binding.progress.setVisibility(GONE);

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ActivePassengers activePassengers= data.getValue(ActivePassengers.class);

                    activePassengersList.add(activePassengers);
                }

                if (activePassengersList.size() > 0) {

                    adapter = new BookRidesAdapter(activePassengersList);

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
