package com.gfaim.activities.groceries;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gfaim.R;
import com.gfaim.activities.groceries.adapter.AddGroceriesViewPagerAdapter;
import com.gfaim.activities.groceries.fragment.AddGroceriesFragment;
import com.gfaim.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class AddGroceriesActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private AddGroceriesViewPagerAdapter adapter;
    private EditText searchEditText;
    private Button addButton;

    private ImageView closeButton;

    private AddGroceriesFragment addGroceriesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_groceries);

        viewPager = findViewById(R.id.viewPager);
        searchEditText = findViewById(R.id.searchEditTextAdd);
        addButton = findViewById(R.id.addButton);


        // Adapter pour le RecyclerView dans ViewPager
        adapter = new AddGroceriesViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        addGroceriesFragment = (AddGroceriesFragment) adapter.createFragment(0);



        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                addGroceriesFragment.filterItems(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        addButton.setVisibility(View.GONE);

        addButton.setOnClickListener(v -> {
            int currentTab = getIntent().getIntExtra("CURRENT_TAB", 0);

            List<FoodItem> selectedItems = addGroceriesFragment.getSelectedItems();

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Aucun élément sélectionné", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_ITEMS", new ArrayList<>(selectedItems));
            resultIntent.putExtra("CURRENT_TAB", currentTab);


            setResult(RESULT_OK, resultIntent);
            finish();
        });

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

    }

    public void toggleSendButtonVisibility(boolean isChecked) {
        if (isChecked) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
        }
    }

}
