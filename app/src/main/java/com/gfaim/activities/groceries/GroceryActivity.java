package com.gfaim.activities.groceries;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.gfaim.R;

public class GroceryActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView tvFridge, tvShopping;
    private ViewPagerAdapter adapter;

    private Button validateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groceries);

        viewPager = findViewById(R.id.viewPager);
        tvFridge = findViewById(R.id.tvFridge);
        tvShopping = findViewById(R.id.tvShopping);

        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        tvFridge.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            updateTabs(0);
        });

        tvShopping.setOnClickListener(v -> {
            viewPager.setCurrentItem(1);
            updateTabs(1);
        });

        // Synchroniser le changement de page avec les onglets
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabs(position);
            }
        });



        // Initialisation des onglets
        updateTabs(0);


    }

    private void updateTabs(int position) {
        if (position == 0) {
            tvFridge.setBackgroundResource(R.drawable.tab_selected);
            tvFridge.setTextColor(Color.WHITE);
            tvShopping.setBackgroundResource(R.drawable.tab_unselected);
            tvShopping.setTextColor(Color.BLACK);
        } else {
            tvFridge.setBackgroundResource(R.drawable.tab_unselected);
            tvFridge.setTextColor(Color.BLACK);
            tvShopping.setBackgroundResource(R.drawable.tab_selected);
            tvShopping.setTextColor(Color.WHITE);
        }
    }

}
