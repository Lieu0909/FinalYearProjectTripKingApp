package com.example.trip.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.trip.ui.MyTrip.DynamicDayFragment;

public class TripScheduleAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public TripScheduleAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return DynamicDayFragment.addfrag(position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
