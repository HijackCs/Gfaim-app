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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.gfaim.R;
import com.gfaim.activities.groceries.adapter.GroceriesViewPagerAdapter;
import com.gfaim.activities.groceries.fragment.FridgeFragment;
import com.gfaim.activities.groceries.fragment.ShoppingFragment;
import com.gfaim.api.ApiClient;
import com.gfaim.api.ShoppingService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.FoodItem;
import com.gfaim.models.groceries.AddShoppingItemRequest;
import com.gfaim.models.groceries.ShoppingItemResponse;
import com.gfaim.models.groceries.ShoppingListResponse;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groceries);

        viewPager = findViewById(R.id.viewPager);
        tvFridge = findViewById(R.id.tvFridge);
        tvShopping = findViewById(R.id.tvShopping);
        EditText searchEditText = findViewById(R.id.searchEditText); // EditText pour la recherche


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
        Log.d(TAG, "Démarrage de sendSelectedItemsToFridge");
        ShoppingFragment shoppingFragment = (ShoppingFragment) adapter.getFragment(1);

        if (shoppingFragment == null) {
            Toast.makeText(this, "Erreur : fragment shopping non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }

        List<FoodItem> selectedItems = new ArrayList<>(shoppingFragment.getSelectedItems());
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Aucun élément sélectionné", Toast.LENGTH_SHORT).show();
            return;
        }

        // Préparer les données pour l'API
        List<AddShoppingItemRequest> requestItems = new ArrayList<>();
        for (FoodItem item : selectedItems) {
            // Utiliser le nom de l'ingrédient en BDD et non le nom localisé
            requestItems.add(new AddShoppingItemRequest(item.getIngredientName()));
        }

        Log.d(TAG, "Nombre d'éléments à marquer comme achetés: " + requestItems.size());

        // Appel à l'API
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        // Utiliser le nouvel endpoint qui gère à la fois l'ajout au frigo et la suppression de la liste de courses
        Call<Void> call = shoppingService.markIngredientsAsBought(token, 1L, requestItems);
        Log.d(TAG, "Appel API en cours...");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Ajouter des logs pour débugger
                Log.d(TAG, "Réponse reçue de markIngredientsAsBought");
                Log.d(TAG, "Code de réponse: " + response.code());
                Log.d(TAG, "Est-ce que la réponse est réussie: " + response.isSuccessful());

                if (response.isSuccessful()) {
                    Log.d(TAG, "Opération réussie, effacement des sélections");
                    shoppingFragment.clearSelectedItems();
                    toggleSendButtonVisibility(false);

                    // Mettre un délai avant de rafraîchir les listes pour laisser le temps au serveur de traiter la requête
                    Log.d(TAG, "Attente avant rafraîchissement des listes...");
                    viewPager.postDelayed(() -> {
                        Log.d(TAG, "Rafraîchissement des listes après délai");
                        // Rafraîchir les deux listes
                        refreshShoppingList();
                        refreshFridgeList();

                        // Basculer vers l'onglet Frigo pour montrer les changements
                        viewPager.setCurrentItem(0);
                    }, 500); // 500ms de délai

                    Toast.makeText(GroceryActivity.this, "Ingrédients marqués comme achetés et ajoutés au frigo", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Erreur lors du traitement: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Corps d'erreur: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Impossible de lire le corps d'erreur: " + e.getMessage());
                    }
                    Toast.makeText(GroceryActivity.this, "Erreur lors du traitement des ingrédients: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Échec de l'appel API: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(GroceryActivity.this, "Erreur de communication: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            int targetTab = data.getIntExtra("CURRENT_TAB", 0);

            // Si on revient de l'écran d'ajout d'ingrédients et que l'onglet cible est "Shopping"
            if (targetTab == 1 && shoppingFragment != null) {
                // Rafraîchir la liste de courses depuis l'API au lieu d'ajouter manuellement les éléments
                refreshShoppingList();
                // Sélectionner l'onglet Shopping
                viewPager.setCurrentItem(1);
            }
            // Si l'onglet cible est "Fridge"
            else if (targetTab == 0 && fridgeFragment != null) {
                // Rafraîchir la liste du frigo depuis l'API
                refreshFridgeList();
                // Sélectionner l'onglet Fridge
                viewPager.setCurrentItem(0);
            }
        }
    }

    // Méthode pour rafraîchir la liste de courses depuis l'API
    private void refreshShoppingList() {
        Log.d(TAG, "Début de refreshShoppingList");
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<ShoppingListResponse> call = shoppingService.getShoppingList(token, 1L);
        call.enqueue(new Callback<ShoppingListResponse>() {
            @Override
            public void onResponse(Call<ShoppingListResponse> call, Response<ShoppingListResponse> response) {
                Log.d(TAG, "refreshShoppingList - Code réponse: " + response.code());
                Log.d(TAG, "refreshShoppingList - Succès: " + response.isSuccessful());
                Log.d(TAG, "refreshShoppingList - Body null: " + (response.body() == null));

                if (response.isSuccessful() && response.body() != null) {
                    ShoppingFragment shoppingFragment = (ShoppingFragment) adapter.getFragment(1);
                    if (shoppingFragment != null) {
                        Log.d(TAG, "refreshShoppingList - Fragment trouvé, mise à jour de la liste");
                        shoppingFragment.updateShoppingList(response.body().getItems());
                    } else {
                        Log.e(TAG, "refreshShoppingList - Fragment introuvable");
                    }
                } else {
                    Log.e(TAG, "refreshShoppingList - Erreur: " + response.code());
                    Toast.makeText(GroceryActivity.this, "Erreur lors de l'actualisation de la liste: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShoppingListResponse> call, Throwable t) {
                Log.e(TAG, "refreshShoppingList - Échec: " + t.getMessage());
                Toast.makeText(GroceryActivity.this, "Erreur de communication: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour rafraîchir la liste du frigo depuis l'API
    private void refreshFridgeList() {
        Log.d(TAG, "Début de refreshFridgeList");
        TokenManager tokenManager = new TokenManager(this);
        ShoppingService shoppingService = ApiClient.getClient(this).create(ShoppingService.class);
        String token = "Bearer " + tokenManager.getAccessToken();

        Call<ShoppingListResponse> call = shoppingService.getFridgeList(token, 1L);
        call.enqueue(new Callback<ShoppingListResponse>() {
            @Override
            public void onResponse(Call<ShoppingListResponse> call, Response<ShoppingListResponse> response) {
                Log.d(TAG, "refreshFridgeList - Code réponse: " + response.code());
                Log.d(TAG, "refreshFridgeList - Succès: " + response.isSuccessful());
                Log.d(TAG, "refreshFridgeList - Body null: " + (response.body() == null));

                if (response.isSuccessful() && response.body() != null) {
                    FridgeFragment fridgeFragment = (FridgeFragment) adapter.getFragment(0);
                    if (fridgeFragment != null) {
                        Log.d(TAG, "refreshFridgeList - Fragment trouvé, mise à jour de la liste");
                        fridgeFragment.updateFridgeList(response.body().getItems());
                    } else {
                        Log.e(TAG, "refreshFridgeList - Fragment introuvable");
                    }
                } else {
                    Log.e(TAG, "refreshFridgeList - Erreur: " + response.code());
                    Toast.makeText(GroceryActivity.this, "Erreur lors de l'actualisation du frigo: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShoppingListResponse> call, Throwable t) {
                Log.e(TAG, "refreshFridgeList - Échec: " + t.getMessage());
                Toast.makeText(GroceryActivity.this, "Erreur de communication: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

