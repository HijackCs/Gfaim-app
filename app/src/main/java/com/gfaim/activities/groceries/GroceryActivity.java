package com.gfaim.activities.groceries;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.gfaim.R;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class GroceryActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView tvFridge, tvShopping;
    private ViewPagerAdapter adapter;

    private Button sendButton;
    private EditText searchEditText;
    private ShoppingFragment shoppingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groceries);

        viewPager = findViewById(R.id.viewPager);
        tvFridge = findViewById(R.id.tvFridge);
        tvShopping = findViewById(R.id.tvShopping);
        searchEditText = findViewById(R.id.searchEditText); // EditText pour la recherche


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

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabs(position);
            }
        });


        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendSelectedItemsToFridge());

        shoppingFragment = (ShoppingFragment) adapter.getFragment(1); // Accéder au fragment Shopping
// Ajouter un TextWatcher pour filtrer les éléments de la liste en fonction de la recherche
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d("GroceryActivity", "Recherche : " + charSequence.toString());  // Ajout d'un log

                if (shoppingFragment != null) {
                    shoppingFragment.filterItems(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
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

    public void toggleSendButtonVisibility(boolean isChecked) {
        if (isChecked) {
            sendButton.setVisibility(View.VISIBLE);
        } else {
            sendButton.setVisibility(View.GONE);
        }
    }


    private void sendSelectedItemsToFridge() {
        ShoppingFragment shoppingFragment = (ShoppingFragment) adapter.getFragment(1);
        FridgeFragment fridgeFragment = (FridgeFragment) adapter.getFragment(0);

        if (shoppingFragment == null || fridgeFragment == null) {
            Toast.makeText(this, "Erreur : fragments non trouvés", Toast.LENGTH_SHORT).show();
            return;
        }

        List<FoodItem> selectedItems = shoppingFragment.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Aucun élément sélectionné", Toast.LENGTH_SHORT).show();
            return;
        }

        fridgeFragment.addItemsToFridge(selectedItems);

        shoppingFragment.removeSelectedItems(selectedItems);

        toggleSendButtonVisibility(false);
    }

}