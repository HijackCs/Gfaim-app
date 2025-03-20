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
import com.gfaim.activities.calendar.model.Ingredient;

import java.util.List;

public class SummaryFragment extends Fragment {

    private SharedStepsViewModel sharedStepsViewModel;
    private String selectedDate;
    private String mealType;
    private int cardPosition;

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
        TextView caloriesTextView = view.findViewById(R.id.caloriesText);
        TextView timeTextView = view.findViewById(R.id.timeText);
        LinearLayout ingredientsList = view.findViewById(R.id.ingredients_list);
        LinearLayout stepsList = view.findViewById(R.id.steps_list);

        // Récupérer les arguments de navigation
        Bundle args = getArguments();
        Log.d("SummaryFragment", "Arguments reçus: " + (args != null ? args.toString() : "null"));

        if (args != null) {
            // Récupérer les arguments avec des valeurs par défaut
            selectedDate = args.getString("selectedDate");
            mealType = args.getString("mealType");
            cardPosition = args.getInt("cardPosition", -1);

            Log.d("SummaryFragment", "Date sélectionnée: " + selectedDate);
            Log.d("SummaryFragment", "Type de repas: " + mealType);
            Log.d("SummaryFragment", "Position de la carte: " + cardPosition);

            // Vérifier si les valeurs sont valides
            if (selectedDate == null || selectedDate.isEmpty()) {
                Log.e("SummaryFragment", "La date est nulle ou vide");
            }
            if (mealType == null || mealType.isEmpty()) {
                Log.e("SummaryFragment", "Le type de repas est nul ou vide");
            }
        } else {
            Log.e("SummaryFragment", "Aucun argument reçu");
        }

        // Observer les changements du nom du menu
        menuNameTextView.setText(sharedStepsViewModel.getMenuName());
        Log.d("SummaryFragment", "Nom du menu: " + sharedStepsViewModel.getMenuName());

        // Observer le nombre de participants
        participantCountTextView.setText(String.valueOf(sharedStepsViewModel.getParticipantCount()));
        Log.d("SummaryFragment", "Nombre de participants: " + sharedStepsViewModel.getParticipantCount());

        // Observer les calories totales
        caloriesTextView.setText(sharedStepsViewModel.getTotalCalories() + " kcal");
        Log.d("SummaryFragment", "Calories totales: " + sharedStepsViewModel.getTotalCalories());

        // Observer la durée totale
        timeTextView.setText(sharedStepsViewModel.getTotalDuration() + " min");
        Log.d("SummaryFragment", "Durée totale: " + sharedStepsViewModel.getTotalDuration());

        // Observer les ingrédients
        sharedStepsViewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            Log.d("SummaryFragment", "Observer - Ingrédients reçus : " + ingredients.size());
            ingredientsList.removeAllViews();
            if (ingredients.isEmpty()) {
                TextView emptyText = new TextView(requireContext());
                emptyText.setText("No ingredients added");
                emptyText.setTextSize(16);
                ingredientsList.addView(emptyText);
            } else {
                for (Ingredient ingredient : ingredients) {
                    TextView ingredientTextView = new TextView(requireContext());
                    ingredientTextView.setText(ingredient.getName() + " (" + ingredient.getCalories() + " kcal)");
                    ingredientTextView.setTextSize(16);
                    ingredientTextView.setPadding(0, 8, 0, 8);
                    ingredientsList.addView(ingredientTextView);
                }
            }
        });

        // Observer les étapes
        sharedStepsViewModel.getSteps().observe(getViewLifecycleOwner(), steps -> {
            Log.d("SummaryFragment", "Observer - Étapes reçues : " + (steps != null ? steps.size() : 0));
            stepsList.removeAllViews();
            if (steps == null || steps.isEmpty()) {
                TextView emptyText = new TextView(requireContext());
                emptyText.setText("No steps added");
                emptyText.setTextSize(16);
                stepsList.addView(emptyText);
            } else {
                for (String step : steps) {
                    TextView stepView = new TextView(requireContext());
                    stepView.setText(step);
                    stepView.setTextSize(16);
                    stepView.setPadding(0, 8, 0, 8);
                    stepsList.addView(stepView);
                }
            }
        });

        // Navigation
        Button finishButton = view.findViewById(R.id.finish);
        finishButton.setOnClickListener(v -> {
            Log.d("SummaryFragment", "Bouton Finish cliqué");
            Log.d("SummaryFragment", "Date actuelle: " + selectedDate);
            Log.d("SummaryFragment", "Type de repas actuel: " + mealType);

            // Vérifier que nous avons toutes les informations nécessaires
            if (selectedDate == null || selectedDate.isEmpty()) {
                Log.e("SummaryFragment", "Date manquante");
                return;
            }
            if (mealType == null || mealType.isEmpty()) {
                Log.e("SummaryFragment", "Type de repas manquant");
                return;
            }

            // Créer un bundle avec les informations du meal
            Bundle mealInfo = new Bundle();
            mealInfo.putString("menuName", sharedStepsViewModel.getMenuName());
            mealInfo.putInt("calories", sharedStepsViewModel.getTotalCalories());
            mealInfo.putInt("duration", sharedStepsViewModel.getTotalDuration());
            mealInfo.putString("selectedDate", selectedDate);
            mealInfo.putString("mealType", mealType);
            mealInfo.putInt("cardPosition", cardPosition);

            Log.d("SummaryFragment", "Navigation vers le calendrier avec les infos: " + mealInfo);

            // Réinitialiser le ViewModel
            sharedStepsViewModel.reset();

            // Naviguer vers le calendrier avec les informations du meal
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_summary_to_calendar, mealInfo);
        });

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }
}
