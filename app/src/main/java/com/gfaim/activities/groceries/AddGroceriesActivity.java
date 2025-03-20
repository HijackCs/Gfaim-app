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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_groceries);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        EditText searchEditText = findViewById(R.id.searchEditTextAdd);
        addButton = findViewById(R.id.addButton);

        // Adapter pour le RecyclerView dans ViewPager
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
                Toast.makeText(this, "Aucun élément sélectionné", Toast.LENGTH_SHORT).show();
                return;
            }

            // Préparer les données pour l'API
            List<AddShoppingItemRequest> requestItems = new ArrayList<>();
            for (FoodItem item : selectedItems) {
                requestItems.add(new AddShoppingItemRequest(item.getIngredientName()));
            }

            // Récupérer l'onglet d'origine (0 = Fridge, 1 = Shopping)
            int currentTab = getIntent().getIntExtra("CURRENT_TAB", 0);

            // Appel à l'API selon l'onglet actif
            if (currentTab == 0) {
                // Ajouter au frigo
                addIngredientsToFridge(selectedItems, requestItems);
            } else {
                // Ajouter à la liste de courses
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

        // Utiliser la version flexible qui renvoie Object pour le frigo
        Call<Object> call = shoppingService.addIngredientsToFridgeFlexible(token, 1L, requestItems);
        Log.d(TAG, "Appel API Frigo Flexible en cours: " + call.request().url());

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, "Réponse Frigo reçue - Code: " + response.code());

                try {
                    if (response.errorBody() != null) {
                        Log.e(TAG, "Corps d'erreur: " + response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur lors de la lecture du corps d'erreur: " + e.getMessage(), e);
                }

                if (response.isSuccessful()) {
                    Log.d(TAG, "Réponse réussie: " + (response.body() != null ? response.body().toString() : "null"));

                    // Retourner les éléments sélectionnés à l'activité précédente
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SELECTED_ITEMS", new ArrayList<>(selectedItems));
                    resultIntent.putExtra("CURRENT_TAB", 0);  // 0 = Frigo
                    setResult(RESULT_OK, resultIntent);
                    finish();

                    Toast.makeText(AddGroceriesActivity.this, "Ingrédients ajoutés au frigo avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddGroceriesActivity.this, "Erreur lors de l'ajout des ingrédients au frigo: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Erreur lors de l'ajout au frigo: " + t.getMessage(), t);
                Toast.makeText(AddGroceriesActivity.this, "Erreur de communication: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addIngredientsToShoppingList(List<FoodItem> selectedItems, List<AddShoppingItemRequest> requestItems) {
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<List<ShoppingListResponse>> call = shoppingService.addIngredientsToShoppingList(token, 1L, requestItems);
        Log.d(TAG, "Appel API Shopping en cours: " + call.request().url());

        call.enqueue(new Callback<List<ShoppingListResponse>>() {
            @Override
            public void onResponse(Call<List<ShoppingListResponse>> call, Response<List<ShoppingListResponse>> response) {
                Log.d(TAG, "Réponse Shopping reçue - Code: " + response.code());
                handleApiResponse(response, selectedItems, 1);
            }

            @Override
            public void onFailure(Call<List<ShoppingListResponse>> call, Throwable t) {
                Log.e(TAG, "Erreur lors de l'ajout à la liste de courses: " + t.getMessage(), t);
                Toast.makeText(AddGroceriesActivity.this, "Erreur de communication: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleApiResponse(Response<List<ShoppingListResponse>> response, List<FoodItem> selectedItems, int currentTab) {
        try {
            if (response.errorBody() != null) {
                Log.e(TAG, "Corps d'erreur: " + response.errorBody().string());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la lecture du corps d'erreur: " + e.getMessage(), e);
        }

        if (response.isSuccessful() && response.body() != null) {
            Log.d(TAG, "Taille du corps de la réponse: " + response.body().size());
            if (!response.body().isEmpty()) {
                ShoppingListResponse firstItem = response.body().get(0);
                Log.d(TAG, "Premier élément - familyId: " + firstItem.getFamilyId());
                if (firstItem.getItems() != null) {
                    Log.d(TAG, "Premier élément - nombre d'items: " + firstItem.getItems().size());
                } else {
                    Log.d(TAG, "Premier élément - items est null");
                }
            }

            // Retourner les éléments sélectionnés à l'activité précédente
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_ITEMS", new ArrayList<>(selectedItems));
            resultIntent.putExtra("CURRENT_TAB", currentTab);
            setResult(RESULT_OK, resultIntent);
            finish();

            // Afficher un message approprié
            String destination = (currentTab == 0) ? "au frigo" : "à la liste de courses";
            Toast.makeText(AddGroceriesActivity.this, "Ingrédients ajoutés " + destination + " avec succès", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddGroceriesActivity.this, "Erreur lors de l'ajout des ingrédients: " + response.code(), Toast.LENGTH_SHORT).show();
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
