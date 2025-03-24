package com.gfaim.activities.groceries;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

public class AddGroceriesActivity extends AppCompatActivity {
    private static final String TAG = "AddGroceriesActivity";
    private Button addButton;
    private AddGroceriesFragment addGroceriesFragment;
    

    private MemberSessionBody member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_groceries);

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

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        EditText searchEditText = findViewById(R.id.searchEditTextAdd);
        addButton = findViewById(R.id.addButton);

        AddGroceriesViewPagerAdapter adapter = new AddGroceriesViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        addGroceriesFragment = (AddGroceriesFragment) adapter.createFragment(0);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                addGroceriesFragment.filterItems(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //empty
            }
        });
        addButton.setVisibility(View.GONE);

        addButton.setOnClickListener(v -> {
            List<FoodItem> selectedItems = addGroceriesFragment.getSelectedItems();

            if (selectedItems.isEmpty()) {
                return;
            }

            List<AddShoppingItemRequest> requestItems = new ArrayList<>();
            for (FoodItem item : selectedItems) {
                requestItems.add(new AddShoppingItemRequest(item.getIngredientName()));
            }

            int currentTab = getIntent().getIntExtra("CURRENT_TAB", 0);

            if (currentTab == 0) {
                addIngredientsToFridge(selectedItems, requestItems);
            } else {
                addIngredientsToShoppingList(selectedItems, requestItems);
            }
        });

        ImageView closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void addIngredientsToFridge(List<FoodItem> selectedItems, List<AddShoppingItemRequest> requestItems) {
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<Object> call = shoppingService.addIngredientsToFridgeFlexible(token, member.getFamilyId(), requestItems);
        Log.d(TAG, "API call: " + call.request().url());

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, "Response Frigo received - Code: " + response.code());

                try {
                    if (response.errorBody() != null) {
                        Log.e(TAG, "Corps d'erreur: " + response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error : " + e.getMessage(), e);
                }

                if (response.isSuccessful()) {
                    Log.d(TAG, "Successful : " + (response.body() != null ? response.body().toString() : "null"));

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SELECTED_ITEMS", new ArrayList<>(selectedItems));
                    resultIntent.putExtra("CURRENT_TAB", 0);
                    setResult(RESULT_OK, resultIntent);
                    finish();

                } else {
                    Toast.makeText(AddGroceriesActivity.this, "Erreur lors de l'ajout des ingrédients au frigo: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Error adding to fridge: " + t.getMessage(), t);
                Toast.makeText(AddGroceriesActivity.this, R.string.errorFetching + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addIngredientsToShoppingList(List<FoodItem> selectedItems, List<AddShoppingItemRequest> requestItems) {
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<List<ShoppingListResponse>> call = shoppingService.addIngredientsToShoppingList(token, member.getFamilyId(), requestItems);
        Log.d(TAG, "API call: " + call.request().url());

        call.enqueue(new Callback<List<ShoppingListResponse>>() {
            @Override
            public void onResponse(Call<List<ShoppingListResponse>> call, Response<List<ShoppingListResponse>> response) {
                Log.d(TAG, "Shopping response  received - Code: " + response.code());
                handleApiResponse(response, selectedItems, 1);
            }

            @Override
            public void onFailure(Call<List<ShoppingListResponse>> call, Throwable t) {
                Log.e(TAG, "Error adding to shopping: " + t.getMessage(), t);
                Toast.makeText(AddGroceriesActivity.this, R.string.errorFetching + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiResponse(Response<List<ShoppingListResponse>> response, List<FoodItem> selectedItems, int currentTab) {
        try {
            if (response.errorBody() != null) {
                Log.e(TAG, "Error: " + response.errorBody().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error: " + e.getMessage(), e);
        }

        if (response.isSuccessful() && response.body() != null) {
            if (!response.body().isEmpty()) {
                ShoppingListResponse firstItem = response.body().get(0);
                if (firstItem.getItems() != null) {
                    Log.d(TAG, "first element - number of items: " + firstItem.getItems().size());
                } else {
                    Log.d(TAG, "item is null");
                }
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_ITEMS", new ArrayList<>(selectedItems));
            resultIntent.putExtra("CURRENT_TAB", currentTab);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(AddGroceriesActivity.this, R.string.errorFetching + response.code(), Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleSendButtonVisibility(boolean isChecked) {
        if (isChecked) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
        }
    }
}
