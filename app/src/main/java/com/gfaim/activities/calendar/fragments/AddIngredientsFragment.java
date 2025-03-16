package com.gfaim.activities.calendar.fragments;

import android.os.Bundle;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gfaim.R;

public class AddIngredientsFragment extends Fragment {

    private LinearLayout ingredientContainer;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_ingredients_calendar, container, false);

        // Initialiser le conteneur des ingrédients
        ingredientContainer = view.findViewById(R.id.ingredientContainer);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le NavController
        navController = Navigation.findNavController(view);

        LinearLayout addIngredientLayout = view.findViewById(R.id.add_ingredient_layout);

        // Gérer le clic sur le LinearLayout
        addIngredientLayout.setOnClickListener(v -> {
            navController.navigate(R.id.action_addIngredientsFragment_to_addIngredientFragment);
        });

        // Récupérer l'ingrédient sélectionné depuis les arguments
        Bundle args = getArguments();
        if (args != null) {
            String selectedIngredient = args.getString("selectedIngredient");
            if (selectedIngredient != null) {
                addIngredientToContainer(selectedIngredient);
            }
        }

        ImageView backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            navController.navigateUp(); // Revient au fragment précédent
        });

        // Gérer le clic sur le bouton "Next"
        Button nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_addIngredientsFragment_to_addStepsFragment);
        });
    }

    private void addIngredientToContainer(String ingredient) {
        // Créer un TextView pour afficher l'ingrédient
        TextView textView = new TextView(getContext());
        textView.setText(ingredient);
        textView.setTextSize(16);
        textView.setPadding(16, 8, 16, 8);

        // Ajouter le TextView au LinearLayout
        ingredientContainer.addView(textView);
    }
}
