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
import com.gfaim.activities.calendar.model.Ingredient;
import com.gfaim.activities.recipe.fragments.RecipeStepsFragment;

import java.util.List;

public class RecipeIngredientsFragment extends Fragment implements IngredientAdapter.OnIngredientClickListener {

    private static final String TAG = "RecipeIngredientsFragment";

    private SharedStepsViewModel sharedStepsViewModel;
    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private TextView recipeTitleTextView, recipeSubtitleTextView;
    private TextView caloriesValueTextView, proteinsValueTextView, carbsValueTextView, fatValueTextView;
    private TextView servingsTextView;
    private Button startCookingButton;
    private int servings = 2; // Valeur par défaut

    public static RecipeIngredientsFragment newInstance() {
        return new RecipeIngredientsFragment();
    }

    public static RecipeIngredientsFragment newInstance(String mealType) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putString("mealType", mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

            // Afficher les arguments reçus
            Bundle args = getArguments();
            if (args != null) {
                Log.d(TAG, "Arguments reçus:");
                for (String key : args.keySet()) {
                    Log.d(TAG, key + " = " + args.get(key));
                }
            } else {
                Log.d(TAG, "Aucun argument reçu");
            }

            // Afficher le contenu du ViewModel pour débogage
            debugViewModel();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation du ViewModel", e);
        }
    }

    /**
     * Méthode de débogage pour vérifier les données du ViewModel
     */
    private void debugViewModel() {
        if (sharedStepsViewModel != null) {
            String menuName = sharedStepsViewModel.getMenuName();
            List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();
            List<String> steps = sharedStepsViewModel.getSteps().getValue();

            Log.d(TAG, "ViewModel - Menu name: " + menuName);
            Log.d(TAG, "ViewModel - Ingredients count: " + (ingredients != null ? ingredients.size() : 0));
            if (ingredients != null) {
                for (int i = 0; i < ingredients.size(); i++) {
                    Ingredient ingredient = ingredients.get(i);
                    Log.d(TAG, "Ingredient " + i + ": " + ingredient.getName() + " - " + ingredient.getCalories() + " kcal");
                }
            }
            Log.d(TAG, "ViewModel - Steps count: " + (steps != null ? steps.size() : 0));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        try {
            initViews(view);
            setupRecyclerView();
            observeViewModel();
            updateServingsDisplay();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de la vue", e);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            // Initialiser les vues
            ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
            recipeTitleTextView = view.findViewById(R.id.recipeTitleTextView);
            recipeSubtitleTextView = view.findViewById(R.id.recipeSubtitleTextView);
            caloriesValueTextView = view.findViewById(R.id.caloriesValueTextView);
            proteinsValueTextView = view.findViewById(R.id.proteinsValueTextView);
            carbsValueTextView = view.findViewById(R.id.carbsValueTextView);
            fatValueTextView = view.findViewById(R.id.fatValueTextView);
            servingsTextView = view.findViewById(R.id.servingsTextView);
            startCookingButton = view.findViewById(R.id.startCookingButton);

            // Configurer le RecyclerView
            setupRecyclerView();

            // Mettre à jour l'interface utilisateur avec les données actuelles
            updateUI();

            // Observer les changements dans la liste d'ingrédients
            sharedStepsViewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
                Log.d(TAG, "Mise à jour des ingrédients observée: " + ingredients.size());
                if (ingredientAdapter != null) {
                    ingredientAdapter.setIngredientList(ingredients);
                }
                updateUI(); // Mettre à jour l'interface utilisateur quand les ingrédients changent
            });

            // Observer les changements du nombre de participants
            if (sharedStepsViewModel.getParticipantCount() > 0) {
                servings = sharedStepsViewModel.getParticipantCount();
                updateUI();
            }

            // Configurer le bouton "Start Cooking"
            startCookingButton.setOnClickListener(v -> {
                // Naviguer vers le fragment de préparation ou autre action
                // Pour l'instant, juste un log
                Log.d(TAG, "Start Cooking clicked");
            });

        } catch (Exception e) {
            Log.e(TAG, "Exception dans onViewCreated", e);
        }
    }

    private void initViews(View view) {
        try {
            // Vérifier que la vue n'est pas null
            if (view == null) {
                Log.e(TAG, "View est null dans initViews");
                return;
            }

            recipeTitleTextView = view.findViewById(R.id.recipeTitleTextView);
            recipeSubtitleTextView = view.findViewById(R.id.recipeSubtitleTextView);
            caloriesValueTextView = view.findViewById(R.id.caloriesValueTextView);
            proteinsValueTextView = view.findViewById(R.id.proteinsValueTextView);
            carbsValueTextView = view.findViewById(R.id.carbsValueTextView);
            fatValueTextView = view.findViewById(R.id.fatValueTextView);
            servingsTextView = view.findViewById(R.id.servingsTextView);
            ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
            startCookingButton = view.findViewById(R.id.startCookingButton);

            // Vérifier que le ViewModel n'est pas null
            if (sharedStepsViewModel == null) {
                Log.e(TAG, "SharedStepsViewModel est null dans initViews");
                return;
            }

            // Récupérer le nombre de participants
            int participants = sharedStepsViewModel.getParticipantCount();
            servings = participants > 0 ? participants : 2;

            // Récupérer le nom de la recette du ViewModel avec vérification
            String menuName = sharedStepsViewModel.getMenuName();
            if (menuName != null && !menuName.isEmpty() && recipeTitleTextView != null) {
                recipeTitleTextView.setText(menuName);
            } else {
                Log.d(TAG, "Menu name is empty or null");
            }

            // Définir le sous-titre avec vérification
            if (recipeSubtitleTextView != null) {
                String subtitle = "Lunch / " + sharedStepsViewModel.getTotalDuration() + " mins";
                recipeSubtitleTextView.setText(subtitle);
            }

            // Calculer les calories totales avec vérification
            if (caloriesValueTextView != null) {
                int totalCalories = sharedStepsViewModel.getTotalCalories();
                caloriesValueTextView.setText(totalCalories + " k");
            }

            // Pour les autres valeurs nutritionnelles, vérifier aussi
            if (proteinsValueTextView != null && carbsValueTextView != null && fatValueTextView != null) {
                // Valeurs statiques pour l'exemple
                proteinsValueTextView.setText("15 g");
                carbsValueTextView.setText("58 g");
                fatValueTextView.setText("20 g");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans initViews", e);
        }
    }

    private void setupRecyclerView() {
        try {
            if (requireActivity() != null && ingredientsRecyclerView != null) {
                Log.d(TAG, "Configuration du RecyclerView");

                // Création de l'adaptateur
                ingredientAdapter = new IngredientAdapter(requireActivity(), this);

                // Configuration du LayoutManager
                ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

                // Définir un ItemDecoration pour l'espacement
                ingredientsRecyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                        requireActivity(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));

                // Définir l'adaptateur
                ingredientsRecyclerView.setAdapter(ingredientAdapter);

                // Forcer les ingrédients initiaux s'ils sont déjà disponibles
                List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();
                if (ingredients != null && !ingredients.isEmpty()) {
                    Log.d(TAG, "Chargement initial de " + ingredients.size() + " ingrédients");
                    ingredientAdapter.setIngredientList(ingredients);
                } else {
                    Log.d(TAG, "Aucun ingrédient initial disponible");
                }
            } else {
                Log.e(TAG, "Context ou RecyclerView null dans setupRecyclerView");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans setupRecyclerView", e);
        }
    }

    private void observeViewModel() {
        try {
            if (sharedStepsViewModel != null && ingredientAdapter != null) {
                Log.d(TAG, "Configuration de l'observation du ViewModel");

                // Observer les changements sur la liste d'ingrédients
                sharedStepsViewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
                    if (ingredients != null) {
                        Log.d(TAG, "Mise à jour des ingrédients, nombre: " + ingredients.size());
                        ingredientAdapter.setIngredientList(ingredients);
                    } else {
                        Log.d(TAG, "Liste d'ingrédients mise à jour est null");
                    }
                });
            } else {
                Log.e(TAG, "ViewModel ou adapter null dans observeViewModel");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans observeViewModel", e);
        }
    }

    @Override
    public void onIngredientClick(Ingredient ingredient) {
        // Action à effectuer lors du clic sur un ingrédient
        if (ingredient != null) {
            Log.d(TAG, "Ingrédient cliqué: " + ingredient.getName());
        }
    }

    private void setupListeners() {
        try {
            View view = getView();
            if (view == null) {
                Log.e(TAG, "View est null dans setupListeners");
                return;
            }

            startCookingButton.setOnClickListener(v -> {
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
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans navigateToStepsFragment", e);
        }
    }

    /**
     * Met à jour l'interface utilisateur avec les données actuelles du ViewModel
     */
    private void updateUI() {
        try {
            // Mettre à jour le titre de la recette
            String menuName = sharedStepsViewModel.getMenuName();
            if (menuName != null && !menuName.isEmpty()) {
                recipeTitleTextView.setText(menuName);
                Log.d(TAG, "Titre de la recette mis à jour: " + menuName);
            } else {
                recipeTitleTextView.setText("Recette sans nom");
                Log.d(TAG, "Aucun nom de recette trouvé, utilisation du nom par défaut");
            }

            // Mettre à jour le sous-titre avec mealType et durée
            String mealType = "Repas";
            Bundle args = getArguments();
            if (args != null && args.containsKey("mealType")) {
                mealType = args.getString("mealType", "Repas");
            }

            int duration = sharedStepsViewModel.getTotalDuration();
            recipeSubtitleTextView.setText(mealType + " / " + duration + " min");
            Log.d(TAG, "Sous-titre mis à jour: " + mealType + " / " + duration + " min");

            // Mettre à jour les informations nutritionnelles
            List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();
            if (ingredients != null && !ingredients.isEmpty()) {
                int totalCalories = 0;

                // Calculer les calories totales
                for (Ingredient ingredient : ingredients) {
                    totalCalories += ingredient.getCalories();
                }

                // Afficher les calories totales
                caloriesValueTextView.setText(totalCalories + " kcal");

                // Pour l'instant, nous utilisons des valeurs fictives pour les autres données nutritionnelles
                // Dans une application réelle, ces valeurs seraient calculées à partir des ingrédients
                proteinsValueTextView.setText("20g");
                carbsValueTextView.setText("30g");
                fatValueTextView.setText("10g");

                Log.d(TAG, "Informations nutritionnelles mises à jour: " + totalCalories + " kcal");
            } else {
                // Valeurs par défaut si aucun ingrédient n'est disponible
                caloriesValueTextView.setText("0 kcal");
                proteinsValueTextView.setText("0g");
                carbsValueTextView.setText("0g");
                fatValueTextView.setText("0g");

                Log.d(TAG, "Aucun ingrédient trouvé, informations nutritionnelles réinitialisées");
            }

            // Mettre à jour le nombre de portions
            servingsTextView.setText(String.valueOf(servings));

            Log.d(TAG, "Interface utilisateur mise à jour avec succès");
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateUI", e);
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
}