package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.activities.calendar.adapter.IngredientAdapter;
import com.gfaim.api.ApiClient;
import com.gfaim.api.IngredientCatalogService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.FoodItem;
import com.gfaim.models.IngredientCatalogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddIngredientFragment extends Fragment {

    private static final String TAG = "AddIngredientFragment";
    private List<FoodItem> selectedIngredients = new ArrayList<>();
    private List<FoodItem> ingredients = new ArrayList<>();
    private SharedStepsViewModel sharedStepsViewModel;
    private RecyclerView recyclerView;
    private IngredientAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        recyclerView = view.findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialiser l'adaptateur une seule fois
        adapter = new IngredientAdapter(ingredients, this::onIngredientSelected);
        recyclerView.setAdapter(adapter);

        loadIngredientsFromAPI();

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> navController.navigateUp());
    }

    private void onIngredientSelected(FoodItem ingredient) {
        if (!selectedIngredients.contains(ingredient)) {
            selectedIngredients.add(ingredient);
            sharedStepsViewModel.addIngredient(ingredient);
            Toast.makeText(getContext(), ingredient.getName() + " ajouté !", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadIngredientsFromAPI() {
        IngredientCatalogService service = ApiClient.getClient(requireContext()).create(IngredientCatalogService.class);

        Log.d(TAG, "Début du chargement des ingrédients");
        Call<List<IngredientCatalogItem>> call = service.getIngredientCatalog();

        call.enqueue(new Callback<List<IngredientCatalogItem>>() {
            @Override
            public void onResponse(Call<List<IngredientCatalogItem>> call, Response<List<IngredientCatalogItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<IngredientCatalogItem> catalogItems = response.body();
                    Log.d(TAG, "Nombre d'ingrédients reçus: " + catalogItems.size());

                    // Conversion optimisée avec Stream API
                    ingredients = catalogItems.stream()
                            .map(item -> new FoodItem(
                                    item.getNameFr(),
                                    item.getNameEn(),
                                    item.getName(),
                                    item.getId()
                            ))
                            .collect(Collectors.toList());

                    // Mise à jour de l'adaptateur
                    adapter.updateList(ingredients);
                    Log.d(TAG, "Liste d'ingrédients mise à jour");

                } else {
                    String errorCode = String.valueOf(response.code());
                    Log.e(TAG, "Erreur lors du chargement des ingrédients: " + errorCode);
                }
            }

            @Override
            public void onFailure(Call<List<IngredientCatalogItem>> call, Throwable t) {
                Log.e(TAG, "Erreur de connexion: " + t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_ingredient, container, false);
    }
}