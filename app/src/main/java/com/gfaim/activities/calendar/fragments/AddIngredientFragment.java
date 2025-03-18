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
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.activities.calendar.adapter.IngredientAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddIngredientFragment extends Fragment {

    private List<String> selectedIngredients = new ArrayList<>();
    private SharedStepsViewModel sharedStepsViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.mealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Tomato");
        ingredients.add("Cheese");
        ingredients.add("Lettuce");
        ingredients.add("Chicken");
        ingredients.add("Onion");

        IngredientAdapter adapter = new IngredientAdapter(ingredients, ingredient -> {
            selectedIngredients.add(ingredient);
            sendSelectedIngredients();
        });
        String selectedIngredient = sharedStepsViewModel.getIngredients().toString(); // Récupère l'ingrédient sélectionné
        if (!selectedIngredient.isEmpty()) {
            sharedStepsViewModel.addIngredient(selectedIngredient);
        }
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            navController.navigateUp();
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_ingredient, container, false);
    }

    private void sendSelectedIngredients() {
        Bundle result = new Bundle();
        result.putStringArrayList("selectedIngredients", new ArrayList<>(selectedIngredients));
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }
}