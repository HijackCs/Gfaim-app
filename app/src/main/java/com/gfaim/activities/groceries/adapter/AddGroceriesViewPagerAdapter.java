package com.gfaim.activities.groceries.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gfaim.activities.groceries.fragment.AddGroceriesFragment;

import java.util.HashMap;
import java.util.Map;

public class AddGroceriesViewPagerAdapter extends FragmentStateAdapter {
    private final Fragment fragment;

    public AddGroceriesViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        fragment = new AddGroceriesFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragment;

    }

    @Override
    public int getItemCount() {
        return 1;
    }

}