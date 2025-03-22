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
    private LinearLayout ingredientsList;
    private LinearLayout stepsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);
        ingredientsList = view.findViewById(R.id.ingredients_list);
        stepsList = view.findViewById(R.id.steps_list);

        // Bouton de retour
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        // Bouton de validation
        Button validateButton = view.findViewById(R.id.finish);
        validateButton.setOnClickListener(v -> {
            // Récupérer les arguments
            Bundle currentArgs = getArguments();
            if (currentArgs != null) {
                String selectedDate = currentArgs.getString("selectedDate");
                String mealType = currentArgs.getString("mealType");
                String parentMeal = currentArgs.getString("parentMeal");
                int cardPosition = currentArgs.getInt("cardPosition", -1);

                // Récupérer le nom du menu directement depuis le ViewModel
                String menuName = sharedStepsViewModel.getMenuName();

                // Calculer la durée totale
                int totalDuration = 0;
                List<Integer> durations = sharedStepsViewModel.getDurations().getValue();
                if (durations != null) {
                    for (Integer duration : durations) {
                        totalDuration += duration;
                    }
                }

                // Calculer les calories totales
                int totalCalories = 0;
                List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();
                if (ingredients != null) {
                    for (Ingredient ingredient : ingredients) {
                        totalCalories += ingredient.getCalories();
                    }
                }

                // Créer le bundle pour la navigation
                Bundle args = new Bundle();
                args.putString("selectedDate", selectedDate);
                args.putString("mealType", mealType);
                args.putString("parentMeal", parentMeal);
                args.putInt("cardPosition", cardPosition);
                args.putString("menuName", menuName);
                args.putInt("calories", totalCalories);
                args.putInt("duration", totalDuration);

                // Afficher les informations en logs pour déboguer
                Log.d("SummaryFragment", "Navigating back with: Date=" + selectedDate +
                        ", MealType=" + mealType + ", Parent=" + parentMeal +
                        ", MenuName=" + menuName + ", Calories=" + totalCalories +
                        ", Duration=" + totalDuration);

                // Naviguer vers le CalendarFragment
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_summary_to_calendar, args);
            }
        });

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
    }
}
