package com.example.trip.ui.Blogger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.trip.Adapter.SectionPagerBlogAdapter;
import com.example.trip.R;
import com.google.android.material.tabs.TabLayout;

public class HomeBlogFragment extends Fragment {

    View myFragment2;

    ViewPager viewPager2;
    TabLayout tabLayout2;


    public HomeBlogFragment() {
        // Required empty public constructor
    }

    public static HomeBlogFragment getInstance() {
        return new HomeBlogFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myFragment2 = inflater.inflate(R.layout.fragment_home_blog, container, false);

        viewPager2 = myFragment2.findViewById(R.id.viewPager2);
        tabLayout2 = myFragment2.findViewById(R.id.tabLayout2);

        return myFragment2;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager2);
        tabLayout2.setupWithViewPager(viewPager2);

        tabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager2) {
        SectionPagerBlogAdapter adapter = new SectionPagerBlogAdapter(getChildFragmentManager());

        adapter.addFragment(new DrafBlogFragment(), "Draf Blog");
        adapter.addFragment(new PostedBlogFragment(), "Share Blog");


        viewPager2.setAdapter(adapter);
    }
}