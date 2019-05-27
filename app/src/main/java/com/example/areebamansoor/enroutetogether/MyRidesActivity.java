package com.example.areebamansoor.enroutetogether;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.areebamansoor.enroutetogether.adapters.TabsAdapter;
import com.example.areebamansoor.enroutetogether.databinding.ActivityMyRidesBinding;
import com.example.areebamansoor.enroutetogether.fragments.FragmentBookRides;
import com.example.areebamansoor.enroutetogether.fragments.FragmentOfferRides;

public class MyRidesActivity extends AppCompatActivity {

    private ActivityMyRidesBinding binding;
    private TabsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_rides);

        binding.toolbarLayout.toolbar.setTitle("My Rides");
        adapter = new TabsAdapter(getSupportFragmentManager());

        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
}
