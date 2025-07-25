package com.gfaim.activities.recipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.activities.calendar.adapter.IngredientAdapter;
import com.gfaim.activities.recipe.fragments.RecipeStepsFragment;
import com.gfaim.models.FoodItem;

import java.util.List;
import java.util.ArrayList;

public class RecipeIngredientsFragment extends Fragment implements IngredientAdapter.OnIngredientClickListener {

    private static final String TAG = "RecipeIngredientsFragment";
    private static final String ARG_MEAL_TYPE = "mealType";

    private SharedStepsViewModel sharedStepsViewModel;
    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private TextView recipeTitleTextView, recipeSubtitleTextView;
    private TextView caloriesValueTextView, proteinsValueTextView, carbsValueTextView, fatValueTextView;
    private TextView servingsTextView;
    private Button startCookingButton;
    private int servings = 2; // Valeur par défaut
    private String mealType = "Repas";

    public static RecipeIngredientsFragment newInstance() {
        return new RecipeIngredientsFragment();
    }

    public static RecipeIngredientsFragment newInstance(String mealType) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_TYPE, mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);

        // Initialiser les vues
        ingredientsRecyclerView = rootView.findViewById(R.id.ingredientsRecyclerView);
        recipeTitleTextView = rootView.findViewById(R.id.recipeTitleTextView);
        recipeSubtitleTextView = rootView.findViewById(R.id.recipeSubtitleTextView);
        caloriesValueTextView = rootView.findViewById(R.id.caloriesValueTextView);
        proteinsValueTextView = rootView.findViewById(R.id.proteinsValueTextView);
        carbsValueTextView = rootView.findViewById(R.id.carbsValueTextView);
        fatValueTextView = rootView.findViewById(R.id.fatValueTextView);
        servingsTextView = rootView.findViewById(R.id.servingsTextView);
        startCookingButton = rootView.findViewById(R.id.startCookingButton);

        // Configuration du RecyclerView
        setupRecyclerView();

        // Configuration du bouton
        if (startCookingButton != null) {
            startCookingButton.setOnClickListener(v -> loadStepsFragment());
        }

        // Mise à jour de l'interface
        updateUI();

        return rootView;
    }

    private void setupRecyclerView() {
        if (getContext() != null && ingredientsRecyclerView != null) {
            ingredientAdapter = new IngredientAdapter(getContext(), this);
            ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ingredientsRecyclerView.setAdapter(ingredientAdapter);

            // Observer les changements d'ingrédients
            sharedStepsViewModel._ingredients.observe(getViewLifecycleOwner(), ingredients -> {
                if (ingredients != null) {
                    Log.d(TAG, "Mise à jour des ingrédients dans RecyclerView: " + ingredients.size() + " ingrédients");
                    for (FoodItem item : ingredients) {
                        Log.d(TAG, "Ingrédient: " + item.getName());
                    }
                    ingredientAdapter.setIngredientList(ingredients);
                }
            });
        }
    }

    private void updateUI() {
        try {
            // Note: La mise à jour du titre et du sous-titre est désormais gérée par updateTitleAndSubtitle()

            // Mise à jour des informations nutritionnelles avec des vérifications
            if (caloriesValueTextView != null) {
                caloriesValueTextView.setText(sharedStepsViewModel.getCalories() + " kcal");
            }

            if (proteinsValueTextView != null) {
                proteinsValueTextView.setText(sharedStepsViewModel.getProtein() + "g");
            }

            if (carbsValueTextView != null) {
                carbsValueTextView.setText(sharedStepsViewModel.getCarbs() + "g");
            }

            if (fatValueTextView != null) {
                fatValueTextView.setText(sharedStepsViewModel.getFat() + "g");
            }

            // Mise à jour du nombre de portions
            if (servingsTextView != null) {
                int servings = sharedStepsViewModel.getNbServings();
                servingsTextView.setText(String.valueOf(servings));

                // Log des informations mises à jour
                Log.d(TAG, "UI mise à jour avec succès - " +
                        "Portions: " + servings +
                        ", Nutrition: Cal=" + sharedStepsViewModel.getCalories() +
                        ", Prot=" + sharedStepsViewModel.getProtein() +
                        ", Carb=" + sharedStepsViewModel.getCarbs() +
                        ", Gras=" + sharedStepsViewModel.getFat());
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la mise à jour de l'interface: " + e.getMessage(), e);
        }
    }

    private void loadStepsFragment() {
        if (getActivity() != null) {
            RecipeStepsFragment stepsFragment = RecipeStepsFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipe_fragment_container, stepsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onIngredientClick(FoodItem ingredient) {
        // Action à effectuer lors du clic sur un ingrédient
        if (ingredient != null) {
            Log.d(TAG, "Ingrédient cliqué: " + ingredient.getName());
        }
    }

    private void setupListeners() {
        try {
            if (startCookingButton == null) {
                Log.e(TAG, "startCookingButton est null dans setupListeners");
                return;
            }

            startCookingButton.setOnClickListener(v -> {
                Log.d(TAG, "Bouton 'Start cooking' cliqué, navigation vers RecipeStepsFragment");
                // Naviguer vers le fragment des étapes
                navigateToStepsFragment();
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception dans setupListeners", e);
        }
    }

    private void updateServingsDisplay() {
        try {
            if (servingsTextView != null) {
                servingsTextView.setText(servings + " serves");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateServingsDisplay", e);
        }
    }

    private void navigateToStepsFragment() {
        try {
            if (getActivity() != null) {
                // Remplacer ce fragment par le fragment des étapes
                RecipeStepsFragment stepsFragment = RecipeStepsFragment.newInstance();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipe_fragment_container, stepsFragment)
                        .addToBackStack(null)
                        .commit();

                Log.d(TAG, "Navigation vers RecipeStepsFragment réussie");
            } else {
                Log.e(TAG, "getActivity() est null dans navigateToStepsFragment");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans navigateToStepsFragment", e);
        }
    }

    /**
     * Récupère le type de repas (déjeuner, dîner, etc.) à partir des arguments
     * @return Le type de repas ou "Repas" par défaut
     */
    private String getMealTypeFromArguments() {
        try {
            Bundle args = getArguments();
            if (args != null && args.containsKey("mealType")) {
                return args.getString("mealType", "Repas");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération du type de repas", e);
        }
        return "Repas";
    }

    private void updateTitleAndSubtitle() {
        try {
            if (recipeTitleTextView != null && recipeSubtitleTextView != null) {
                // Mettre à jour le titre avec le nom de la recette
                String menuName = sharedStepsViewModel.getMenuName();
                if (menuName != null && !menuName.isEmpty()) {
                    recipeTitleTextView.setText(menuName);
                }

                // Récupérer le type de repas depuis les arguments
                String mealType = getMealTypeFromArguments();

                // Récupérer la durée totale
                int duration = sharedStepsViewModel.getTotalDuration();

                // Formater et afficher le sous-titre (type de repas / durée)
                String subtitle = mealType + " / " + duration + " min";
                recipeSubtitleTextView.setText(subtitle);

                Log.d(TAG, "Sous-titre mis à jour: " + subtitle);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateTitleAndSubtitle", e);
        }
    }

    private void updateNutritionInfo() {
        try {
            if (caloriesValueTextView != null && proteinsValueTextView != null && carbsValueTextView != null && fatValueTextView != null) {
                caloriesValueTextView.setText(sharedStepsViewModel.getCalories() + " kcal");
                proteinsValueTextView.setText(sharedStepsViewModel.getProtein() + "g");
                carbsValueTextView.setText(sharedStepsViewModel.getCarbs() + "g");
                fatValueTextView.setText(sharedStepsViewModel.getFat() + "g");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateNutritionInfo", e);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            // Mettre à jour le titre et le sous-titre après que la vue soit créée
            updateTitleAndSubtitle();
            updateNutritionInfo();

            // Observer les changements dans la durée pour mettre à jour le sous-titre
            sharedStepsViewModel.getDurations().observe(getViewLifecycleOwner(), durations -> {
                updateTitleAndSubtitle();
            });

            Log.d(TAG, "Vue créée, titre et sous-titre mis à jour");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la mise à jour du titre et sous-titre dans onViewCreated", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // Assurer que les données sont à jour quand le fragment devient visible
            updateUI();
            updateTitleAndSubtitle();
            updateNutritionInfo();

            Log.d(TAG, "Fragment repris, interface mise à jour");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la mise à jour de l'interface dans onResume", e);
        }
    }
}