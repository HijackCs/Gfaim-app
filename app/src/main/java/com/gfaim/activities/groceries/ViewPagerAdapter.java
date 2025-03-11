package com.gfaim.activities.groceries;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public ViewPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
        fragmentMap.put(0, new FridgeFragment());
        fragmentMap.put(1, new ShoppingFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentMap.get(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public Fragment getFragment(int position) {
        return fragmentMap.get(position);
    }
}