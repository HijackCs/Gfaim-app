package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.gfaim.activities.calendar.IngredientQuantityDialog;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.activities.calendar.adapter.IngredientAdapter;
import com.gfaim.activities.calendar.model.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddIngredientFragment extends Fragment {

    private List<Ingredient> selectedIngredients = new ArrayList<>();
    private SharedStepsViewModel sharedStepsViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Liste des ingrédients avec leurs calories
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient("Tomato", 22),
                new Ingredient("Cheese", 402),
                new Ingredient("Lettuce", 15),
                new Ingredient("Chicken", 165),
                new Ingredient("Onion", 44)
        );

        // Modification du comportement au clic sur un ingrédient
        IngredientAdapter adapter = new IngredientAdapter(ingredients, ingredient -> {
            // Au lieu d'ajouter directement l'ingrédient, on affiche la popup
            showIngredientQuantityDialog(ingredient);
        });

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            navController.navigateUp();
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * Affiche la popup pour sélectionner la quantité et l'unité de l'ingrédient
     * @param ingredient L'ingrédient sélectionné
     */
    private void showIngredientQuantityDialog(Ingredient ingredient) {
        IngredientQuantityDialog dialog = new IngredientQuantityDialog(
                requireContext(),
                ingredient,
                (selectedIngredient, quantity, unit) -> {
                    // Créer une nouvelle instance de l'ingrédient avec la quantité et l'unité
                    Ingredient ingredientWithQuantity = new Ingredient(
                            selectedIngredient.getName(),
                            selectedIngredient.getCalories(),
                            quantity,
                            unit
                    );

                    // Ajouter l'ingrédient à la liste des ingrédients sélectionnés
                    if (!selectedIngredients.contains(ingredientWithQuantity)) {
                        selectedIngredients.add(ingredientWithQuantity);
                        sharedStepsViewModel.addIngredient(ingredientWithQuantity);
                    }
                }
        );

        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_ingredient, container, false);
    }

}