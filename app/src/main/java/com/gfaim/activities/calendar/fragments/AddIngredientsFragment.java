package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.R;
import com.gfaim.activities.calendar.model.Ingredient;

import java.util.List;

public class AddIngredientsFragment extends Fragment {

    private LinearLayout ingredientContainer;
    private NavController navController;
    private TextView participantCountText;
    private int participantCount = 1;
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 10;
    private SharedStepsViewModel sharedStepsViewModel;
    private TextView participantCountTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_ingredients_calendar, container, false);

        ingredientContainer = view.findViewById(R.id.ingredientContainer);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le NavController
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Initialiser le ViewModel
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

        // Charger et afficher les ingrédients stockés
        updateIngredientList(sharedStepsViewModel.getIngredients().getValue());

        // Observer les mises à jour des ingrédients
        sharedStepsViewModel.getIngredients().observe(getViewLifecycleOwner(), this::updateIngredientList);


        // Ajouter un ingrédient via le bouton
        ImageButton addIngredientButton = view.findViewById(R.id.add_ingredient_button);
        addIngredientButton.setOnClickListener(v -> {
            // Créer un bundle avec les informations nécessaires
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", getArguments().getString("selectedDate"));
            bundle.putString("mealType", getArguments().getString("mealType"));
            bundle.putInt("cardPosition", getArguments().getInt("cardPosition"));

            // Naviguer vers AddIngredientFragment
            navController.navigate(R.id.action_addIngredientsFragment_to_addIngredientFragment, bundle);
        });

        participantCountText = view.findViewById(R.id.participant_count);
        ImageButton removeBtn = view.findViewById(R.id.remove_btn);
        ImageButton addBtn = view.findViewById(R.id.add_btn);

        updateParticipantCount();

        // Gérer le changement du nombre de participants
        removeBtn.setOnClickListener(v -> {
            if (participantCount > MIN_COUNT) {
                participantCount--;
                updateParticipantCount();
            }
        });

        addBtn.setOnClickListener(v -> {
            if (participantCount < MAX_COUNT) {
                participantCount++;
                updateParticipantCount();
            }
        });

        // Restaurer le nom du menu et le nombre de participants
        TextView menuNameEditText = view.findViewById(R.id.menuName);
        menuNameEditText.setText(sharedStepsViewModel.getMenuName());

        participantCountTextView = view.findViewById(R.id.participant_count);
        participantCountTextView.setText(String.valueOf(sharedStepsViewModel.getParticipantCount()));

        // Sauvegarde du menuName
        menuNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                sharedStepsViewModel.setMenuName(menuNameEditText.getText().toString());
            }
        });

        // Sauvegarde du nombre de participants
        participantCountTextView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            sharedStepsViewModel.setParticipantCount(Integer.parseInt(participantCountTextView.getText().toString()));
        });

        // Bouton retour
        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> navController.navigateUp());

        // Bouton suivant
        Button nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(v -> {
            // Sauvegarder le nom du menu avant de naviguer
            String menuName = menuNameEditText.getText().toString();
            sharedStepsViewModel.setMenuName(menuName);

            // Récupérer les arguments actuels
            Bundle currentArgs = getArguments();
            if (currentArgs != null) {
                // Créer un nouveau bundle avec les mêmes informations
                Bundle args = new Bundle();
                args.putString("selectedDate", currentArgs.getString("selectedDate"));
                args.putString("mealType", currentArgs.getString("mealType"));
                args.putString("parentMeal", currentArgs.getString("parentMeal"));
                args.putInt("cardPosition", currentArgs.getInt("cardPosition"));
                args.putString("menuName", menuName);  // Ajouter le nom du menu aux arguments

                // Naviguer vers AddStepsFragment avec les arguments
                navController.navigate(R.id.action_addIngredients_to_addSteps, args);
            } else {
                // Si pas d'arguments, naviguer sans arguments
                navController.navigate(R.id.action_addIngredients_to_addSteps);
            }
        });
    }

    private void updateIngredientList(List<Ingredient> ingredients) {
        ingredientContainer.removeAllViews();
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                addIngredientView(ingredient);
            }
        }
    }


    private void addIngredientView(Ingredient ingredient) {
        LinearLayout ingredientLayout = new LinearLayout(getContext());
        ingredientLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);
        ingredientLayout.setPadding(16, 8, 16, 8);

        // Texte de l'ingrédient
        TextView ingredientText = new TextView(getContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f);
        ingredientText.setLayoutParams(textParams);
        ingredientText.setText(ingredient.getName() + " (" + ingredient.getCalories() + " kcal)");
        ingredientText.setTextSize(16);
        ingredientText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));

        ingredientLayout.addView(ingredientText);
        ingredientContainer.addView(ingredientLayout);
    }

    private void updateParticipantCount() {
        participantCountText.setText(String.valueOf(participantCount));
    }
}
