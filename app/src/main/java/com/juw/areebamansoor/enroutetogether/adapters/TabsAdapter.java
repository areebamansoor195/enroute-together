package com.juw.areebamansoor.enroutetogether.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.juw.areebamansoor.enroutetogether.fragments.FragmentBookRides;
import com.juw.areebamansoor.enroutetogether.fragments.FragmentOfferRides;

public class TabsAdapter extends FragmentPagerAdapter {

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentOfferRides();
            case 1:
                return new FragmentBookRides();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Offer Rides";
            case 1:
                return "Book Rides";
            default:
                return null;
        }
    }
}
