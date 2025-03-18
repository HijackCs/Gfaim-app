package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;

public class SummaryFragment extends Fragment {

    private SharedStepsViewModel sharedStepsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        // Récupération des vues
        TextView menuNameTextView = view.findViewById(R.id.menu_name);
        TextView participantCountTextView = view.findViewById(R.id.participant_count);
        LinearLayout ingredientsList = view.findViewById(R.id.ingredients_list);
        LinearLayout stepsList = view.findViewById(R.id.steps_list);

        // Remplir les champs
        menuNameTextView.setText(sharedStepsViewModel.getMenuName());
        participantCountTextView.setText(String.valueOf(sharedStepsViewModel.getParticipantCount()));

        Log.d("SummaryFragment", "Ingrédients reçus : " + sharedStepsViewModel.getIngredients());

        // Ajouter les ingrédients
        SharedStepsViewModel menuViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        menuViewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            Log.d("SummaryFragment", "Observer - Ingrédients reçus : " + ingredients);
            ingredientsList.removeAllViews();
            if (ingredients.isEmpty()) {
                TextView emptyText = new TextView(getContext());
                emptyText.setText("No ingredients added");
                emptyText.setTextSize(16);
                ingredientsList.addView(emptyText);
            } else {
                for (String ingredient : ingredients) {
                    TextView ingredientTextView = new TextView(getContext());
                    ingredientTextView.setText(ingredient);
                    ingredientTextView.setTextSize(16);
                    ingredientsList.addView(ingredientTextView);
                }
            }
        });


        // Ajouter les étapes
        stepsList.removeAllViews();
        for (String step : sharedStepsViewModel.getSteps()) {
            TextView stepTextView = new TextView(getContext());
            stepTextView.setText(step);
            stepTextView.setTextSize(16);
            stepsList.addView(stepTextView);
        }

        // Navigation
        NavController navController = NavHostFragment.findNavController(this);

        Button finishButton = view.findViewById(R.id.finish);
        finishButton.setOnClickListener(v -> navController.navigate(R.id.action_summary_to_calendar));

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> navController.navigateUp());
    }
}
