package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.adapter.IngredientAdapter;

import java.util.Arrays;
import java.util.List;

public class AddIngredientFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_ingredient, container, false);

        // Initialiser le RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        NavController navController = NavHostFragment.findNavController(this);

        // Liste d'ingrédients (exemple)
        List<String> ingredients = Arrays.asList("Tomato", "Potato", "Carrot", "Onion");

        // Adapter pour le RecyclerView
        IngredientAdapter adapter = new IngredientAdapter(ingredients, ingredient -> {
            Bundle bundle = new Bundle();
            bundle.putString("selectedIngredient", ingredient);
            navController.navigate(R.id.action_addIngredient_to_addIngredients, bundle);
        });
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            navController.navigateUp(); // Revient au fragment précédent
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}