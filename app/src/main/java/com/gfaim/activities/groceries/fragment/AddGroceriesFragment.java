package com.gfaim.activities.groceries.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.groceries.AddGroceriesActivity;
import com.gfaim.activities.groceries.adapter.AddGroceriesAdapter;
import com.gfaim.api.ApiClient;
import com.gfaim.api.IngredientCatalogService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.FoodItem;
import com.gfaim.models.IngredientCatalogItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroceriesFragment extends Fragment {
    private static final String TAG = "AddGroceriesFragment";

    private List<FoodItem> foodList = new ArrayList<>();
    private List<FoodItem> filteredList = new ArrayList<>();
    private List<FoodItem> selectedItems = new ArrayList<>();

    private AddGroceriesAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Initialiser les vues pour l'état de chargement et les états vides
        progressBar = view.findViewById(R.id.progressBar);
        if (progressBar == null) {
            // Si le progressBar n'existe pas dans la mise en page, créons-en un
            progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ((ViewGroup) recyclerView.getParent()).addView(progressBar, layoutParams);
            progressBar.setVisibility(View.GONE);
        }

        emptyTextView = view.findViewById(R.id.emptyTextView);
        if (emptyTextView == null) {
            // Si le texte pour état vide n'existe pas dans la mise en page, nous pouvons le créer
            emptyTextView = new TextView(getContext());
            emptyTextView.setText("Aucun ingrédient disponible");
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ((ViewGroup) recyclerView.getParent()).addView(emptyTextView, layoutParams);
            emptyTextView.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialiser l'adaptateur avec une liste vide
        adapter = new AddGroceriesAdapter(foodList, this);
        recyclerView.setAdapter(adapter);

        // Ajouter un séparateur
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        recyclerView.addItemDecoration(divider);

        // Charger les ingrédients depuis l'API
        loadIngredientsFromAPI();

        return view;
    }

    private void loadIngredientsFromAPI() {
        // Montrer l'indicateur de chargement
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);

        // Créer le service pour accéder à l'API
        TokenManager tokenManager = new TokenManager(requireContext());
        IngredientCatalogService service = ApiClient.getClient(requireContext()).create(IngredientCatalogService.class);

        // Préparer l'appel API
        String token = "Bearer " + tokenManager.getAccessToken();
        Call<List<IngredientCatalogItem>> call = service.getIngredientCatalog(token);

        Log.d(TAG, "Chargement des ingrédients depuis l'API...");

        // Exécuter l'appel API de manière asynchrone
        call.enqueue(new Callback<List<IngredientCatalogItem>>() {
            @Override
            public void onResponse(Call<List<IngredientCatalogItem>> call, Response<List<IngredientCatalogItem>> response) {
                // Cacher l'indicateur de chargement
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<IngredientCatalogItem> catalogItems = response.body();

                    // Convertir les éléments du catalogue en FoodItems
                    foodList.clear();
                    for (IngredientCatalogItem item : catalogItems) {
                        FoodItem foodItem = new FoodItem(
                                item.getNameFr(),
                                item.getNameEn(),
                                item.getName(),
                                item.getId()
                        );
                        foodList.add(foodItem);
                    }

                    // Mettre à jour les listes filtrées
                    filteredList.clear();
                    filteredList.addAll(foodList);

                    // Mettre à jour l'adaptateur
                    adapter.updateList(filteredList);

                    // Afficher la liste si elle n'est pas vide
                    if (foodList.isEmpty()) {
                        if (emptyTextView != null) emptyTextView.setVisibility(View.VISIBLE);
                        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                    } else {
                        if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);
                        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                    }

                    Log.i(TAG, "Nombre d'ingrédients chargés: " + foodList.size());

                } else {
                    // Gérer l'erreur
                    String errorCode = String.valueOf(response.code());
                    Log.e(TAG, "Erreur lors du chargement des ingrédients: " + errorCode);
                    Toast.makeText(getContext(), "Erreur lors du chargement des ingrédients: " + errorCode, Toast.LENGTH_SHORT).show();

                    if (emptyTextView != null) {
                        emptyTextView.setText("Impossible de charger les ingrédients. Erreur: " + errorCode);
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                    if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<IngredientCatalogItem>> call, Throwable t) {
                // Cacher l'indicateur de chargement
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Afficher le message d'erreur
                String errorMessage = t.getMessage();
                Log.e(TAG, "Erreur lors du chargement des ingrédients: " + errorMessage, t);
                Toast.makeText(getContext(), "Erreur de connexion: " + errorMessage, Toast.LENGTH_SHORT).show();

                if (emptyTextView != null) {
                    emptyTextView.setText("Impossible de se connecter au serveur. Veuillez réessayer plus tard.");
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            }
        });
    }

    public void filterItems(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(foodList); // Afficher tous les éléments si la recherche est vide
        } else {
            for (FoodItem item : foodList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateList(filteredList);  // Met à jour la liste filtrée dans l'adaptateur
        adapter.notifyDataSetChanged();
    }

    public void clearSelectedItems() {
        selectedItems.clear();
    }

    public List<FoodItem> getSelectedItems() {
        return selectedItems;
    }

    public void toggleItemSelection(FoodItem item, boolean isChecked) {
        if (isChecked) {
            if (!selectedItems.contains(item)) {
                selectedItems.add(item);
            }
        } else {
            selectedItems.remove(item);
        }

        AddGroceriesActivity activity = (AddGroceriesActivity) getActivity();
        if (activity != null) {
            activity.toggleSendButtonVisibility(!selectedItems.isEmpty());
        }
    }
}

