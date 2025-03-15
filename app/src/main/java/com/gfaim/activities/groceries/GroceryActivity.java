package com.gfaim.activities.groceries;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.gfaim.R;
import com.gfaim.activities.groceries.adapter.GroceriesViewPagerAdapter;
import com.gfaim.activities.groceries.fragment.FridgeFragment;
import com.gfaim.activities.groceries.fragment.ShoppingFragment;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class GroceryActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView tvFridge, tvShopping;
    GroceriesViewPagerAdapter adapter;

    private Button sendButton;
    private EditText searchEditText;
    private ShoppingFragment shoppingFragment;
    private FridgeFragment fridgeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groceries);

        viewPager = findViewById(R.id.viewPager);
        tvFridge = findViewById(R.id.tvFridge);
        tvShopping = findViewById(R.id.tvShopping);
        searchEditText = findViewById(R.id.searchEditText); // EditText pour la recherche


        adapter = new GroceriesViewPagerAdapter(this);
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
        fridgeFragment = (FridgeFragment) adapter.getFragment(0); // Accéder au fragment Fridge

        // Ajouter un TextWatcher pour la recherche
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition == 0 && fridgeFragment != null) {
                    fridgeFragment.filterItems(charSequence.toString());
                } else if (currentPosition == 1 && shoppingFragment != null) {
                    shoppingFragment.filterItems(charSequence.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        ImageView btnAdd = findViewById(R.id.addButton);
        btnAdd.setOnClickListener(v -> {
            int currentTab = viewPager.getCurrentItem(); // 0 = Fridge, 1 = Shopping
            Intent intent = new Intent(getApplicationContext(), AddGroceriesActivity.class);
            intent.putExtra("CURRENT_TAB", currentTab);
            startActivityForResult(intent, 1);
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

        List<FoodItem> selectedItems = new ArrayList<>(shoppingFragment.getSelectedItems());
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Aucun élément sélectionné", Toast.LENGTH_SHORT).show();
            return;
        }

        fridgeFragment.addItemsToFridge(selectedItems);

        for (FoodItem item : selectedItems) {
            shoppingFragment.removeItem(item);
        }

        shoppingFragment.clearSelectedItems();

        toggleSendButtonVisibility(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            int targetTab = data.getIntExtra("CURRENT_TAB", 0);
            ArrayList<FoodItem> receivedItems = (ArrayList<FoodItem>) data.getSerializableExtra("SELECTED_ITEMS");

            if (receivedItems != null && !receivedItems.isEmpty()) {
                if (targetTab == 0 && fridgeFragment != null) {
                    fridgeFragment.addItemsToFridge(receivedItems);
                } else if (targetTab == 1 && shoppingFragment != null) {
                    shoppingFragment.addItemsToShopping(receivedItems);
                }
            }
        }
    }


}

