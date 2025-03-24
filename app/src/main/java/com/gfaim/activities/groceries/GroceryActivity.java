package com.gfaim.activities.groceries;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.gfaim.R;
import com.gfaim.activities.home.HomeActivity;
import com.gfaim.activities.NavigationBar;
import com.gfaim.activities.home.HomeActivity;
import com.gfaim.activities.groceries.adapter.GroceriesViewPagerAdapter;
import com.gfaim.activities.groceries.fragment.FridgeFragment;
import com.gfaim.activities.groceries.fragment.ShoppingFragment;
import com.gfaim.activities.settings.SettingsActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.ShoppingService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.FoodItem;
import com.gfaim.models.groceries.AddShoppingItemRequest;
import com.gfaim.models.groceries.ShoppingListResponse;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroceryActivity extends AppCompatActivity {
    private static final String TAG = "GroceryActivity";
    private ViewPager2 viewPager;
    private TextView tvFridge;
    private TextView tvShopping;
    GroceriesViewPagerAdapter adapter;

    private Button sendButton;
    private ShoppingFragment shoppingFragment;
    private FridgeFragment fridgeFragment;
    
    private MemberSessionBody member;
    private NavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groceries);

        navigationBar = new NavigationBar(this);
        int activeButtonId = getIntent().getIntExtra("activeButtonId", -1);
        if (activeButtonId != -1) {
            navigationBar.setActiveButton(activeButtonId);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        UtileProfile utileProfile = new UtileProfile(this);

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {
                //empty
            }
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
            }
            @Override
            public void onFailure(Throwable error) {
                //empty
            }
            @Override
            public void onSuccess(CreateMember body) {
                //empty
            }
        });

        viewPager = findViewById(R.id.viewPager);
        tvFridge = findViewById(R.id.tvFridge);
        tvShopping = findViewById(R.id.tvShopping);
        EditText searchEditText = findViewById(R.id.searchEditText);


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

        shoppingFragment = (ShoppingFragment) adapter.getFragment(1);
        fridgeFragment = (FridgeFragment) adapter.getFragment(0);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //empty
            }

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
            public void afterTextChanged(Editable editable) {
                //empty
            }
        });

        ImageView btnAdd = findViewById(R.id.addButton);
        btnAdd.setOnClickListener(v -> {
            int currentTab = viewPager.getCurrentItem();
            Intent intent = new Intent(getApplicationContext(), AddGroceriesActivity.class);
            intent.putExtra("CURRENT_TAB", currentTab);
            startActivityForResult(intent, 1);
        });

        updateTabs(0);

        setupProfilBtn();
    }

    private void setupProfilBtn(){
        FrameLayout circleProfile = findViewById(R.id.circleProfile);
        circleProfile.setOnClickListener( v->{
            Intent intent = new Intent(GroceryActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
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

        if (shoppingFragment == null) {
            return;
        }

        List<FoodItem> selectedItems = new ArrayList<>(shoppingFragment.getSelectedItems());
        if (selectedItems.isEmpty()) {
            return;
        }

        List<AddShoppingItemRequest> requestItems = new ArrayList<>();
        for (FoodItem item : selectedItems) {
            requestItems.add(new AddShoppingItemRequest(item.getIngredientName()));
        }


        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<Void> call = shoppingService.markIngredientsAsBought(token, member.getFamilyId(), requestItems);
        Log.d(TAG, "Appel API en cours...");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    shoppingFragment.clearSelectedItems();
                    toggleSendButtonVisibility(false);

                    viewPager.postDelayed(() -> {
                        refreshShoppingList();
                        refreshFridgeList();

                        viewPager.setCurrentItem(0);
                    }, 500);

                } else {
                    Log.e(TAG, "Error: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Impossible to read error: " + e.getMessage());
                    }
                    Toast.makeText(GroceryActivity.this, R.string.errorFetching + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error API call: " + t.getMessage());
                Toast.makeText(GroceryActivity.this, R.string.errorFetching + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            int targetTab = data.getIntExtra("CURRENT_TAB", 0);

            if (targetTab == 1 && shoppingFragment != null) {
                refreshShoppingList();
                viewPager.setCurrentItem(1);
            }
            else if (targetTab == 0 && fridgeFragment != null) {
                refreshFridgeList();
                viewPager.setCurrentItem(0);
            }
        }
    }

    private void refreshShoppingList() {
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<ShoppingListResponse> call = shoppingService.getShoppingList(token, member.getFamilyId());
        call.enqueue(new Callback<ShoppingListResponse>() {
            @Override
            public void onResponse(Call<ShoppingListResponse> call, Response<ShoppingListResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ShoppingFragment shoppingFragment = (ShoppingFragment) adapter.getFragment(1);
                    if (shoppingFragment != null) {
                        shoppingFragment.updateShoppingList(response.body().getItems());
                    } else {
                        Log.e(TAG, "refreshShoppingList - Fragment introuvable");
                    }
                } else {
                    Log.e(TAG, "refreshShoppingList - Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ShoppingListResponse> call, Throwable t) {
                Log.e(TAG, "refreshShoppingList - Error: " + t.getMessage());
                Toast.makeText(GroceryActivity.this, R.string.errorFetching + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshFridgeList() {
        Log.d(TAG, "Début de refreshFridgeList");
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<ShoppingListResponse> call = shoppingService.getFridgeList(token, member.getFamilyId());
        call.enqueue(new Callback<ShoppingListResponse>() {
            @Override
            public void onResponse(Call<ShoppingListResponse> call, Response<ShoppingListResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    FridgeFragment fridgeFragment = (FridgeFragment) adapter.getFragment(0);
                    if (fridgeFragment != null) {
                        fridgeFragment.updateFridgeList(response.body().getItems());
                    } else {
                        Log.e(TAG, "refreshFridgeList - Fragment introuvable");
                    }
                } else {
                    Log.e(TAG, "refreshFridgeList - Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ShoppingListResponse> call, Throwable t) {
                Log.e(TAG, "refreshFridgeList - Échec: " + t.getMessage());
                Toast.makeText(GroceryActivity.this, R.string.errorFetching + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

