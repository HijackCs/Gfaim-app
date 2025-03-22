package com.gfaim.activities.calendar.fragments;

import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.model.Recipe;
import com.gfaim.activities.calendar.adapter.RecipeAdapter;
import com.gfaim.api.ApiClient;
import com.gfaim.api.RecipeService;
import com.gfaim.activities.recipe.fragments.RecipeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseRecipeFragment extends Fragment {
    private static final String TAG = "ChooseRecipeFragment";
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipes;
    private NavController navController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_recipe_calendar, container, false);

        recyclerView = view.findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes);
        recyclerView.setAdapter(adapter);

        // Charger les recettes depuis l'API
        loadRecipesFromApi();

        return view;
    }

    private void loadRecipesFromApi() {
        RecipeService recipeService = ApiClient.getClient(requireContext()).create(RecipeService.class);

        // Récupérer l'ID de la famille de l'utilisateur (à adapter selon votre logique)
        Long familyId = 1L; // Exemple: ID de la famille actuelle

        Call<List<Recipe>> call = recipeService.getRecipeSuggestions(familyId);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipes.clear();
                    recipes.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Recettes chargées avec succès: " + recipes.size());
                } else {
                    Log.e(TAG, "Erreur lors du chargement des recettes: " + response.code());
                    Toast.makeText(getContext(), "Erreur lors du chargement des recettes", Toast.LENGTH_SHORT).show();
                    // Charger des recettes par défaut en cas d'échec
                    loadDefaultRecipes();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Erreur réseau lors du chargement des recettes", t);
                Toast.makeText(getContext(), "Erreur réseau lors du chargement des recettes", Toast.LENGTH_SHORT).show();
                // Charger des recettes par défaut en cas d'échec
                loadDefaultRecipes();
            }
        });
    }

    private void loadDefaultRecipes() {
        recipes.clear();
        recipes.add(new Recipe("Spaghetti Bolognaise",
                Arrays.asList("Pâtes", "Viande hachée", "Sauce tomate"),
                Arrays.asList("Cuire les pâtes", "Préparer la sauce"), 4));
        recipes.add(new Recipe("Salade César",
                Arrays.asList("Laitue", "Poulet", "Parmesan"),
                Arrays.asList("Couper la laitue", "Griller le poulet"), 2));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // Configuration du listener pour le clic sur une recette
        adapter.setOnItemClickListener(position -> {
            Recipe selectedRecipe = recipes.get(position);

            // Créer un Intent pour naviguer vers RecipeActivity
            Intent intent = new Intent(getContext(), RecipeActivity.class);

            // Transmettre l'ID de la recette pour que RecipeActivity puisse récupérer les détails
            intent.putExtra("recipe_id", selectedRecipe.getId());

            // Transmettre d'autres informations utiles
            intent.putExtra("menuName", selectedRecipe.getName());
            intent.putExtra("selectedDate", getArguments().getString("selectedDate"));
            intent.putExtra("mealType", getArguments().getString("mealType"));
            intent.putExtra("duration", selectedRecipe.getReadyInMinutes());
            intent.putExtra("calories", selectedRecipe.getCalories());
            intent.putExtra("cardPosition", getArguments().getInt("cardPosition", -1));

            // Vérifier si c'est un snack et transmettre le repas parent
            String mealType = getArguments().getString("mealType");
            if ("Snack".equals(mealType)) {
                intent.putExtra("parentMeal", getArguments().getString("parentMeal"));
            }

            // Démarrer l'activité
            startActivity(intent);
        });

        // Bouton retour
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> navController.navigateUp());
    }
}
