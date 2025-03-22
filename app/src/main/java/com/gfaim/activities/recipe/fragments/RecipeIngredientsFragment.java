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
        try {
            // Utiliser directement findViewById plutôt que de stocker dans des variables
            View rootView = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);

            // Récupérer le mealType depuis les arguments
            if (getArguments() != null && getArguments().containsKey(ARG_MEAL_TYPE)) {
                mealType = getArguments().getString(ARG_MEAL_TYPE, "Repas");
            }

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

            // Obtenir le ViewModel
            sharedStepsViewModel = new ViewModelProvider(requireActivity()).get(SharedStepsViewModel.class);

            // Configuration simple du RecyclerView
            if (ingredientsRecyclerView != null) {
                ingredientsRecyclerView.setVisibility(View.VISIBLE);
                ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                // Créer un adaptateur simple sans listener
                ingredientAdapter = new IngredientAdapter(new ArrayList<>(), ingredient -> {
                    // Ne rien faire pour l'instant
                });

                // Définir l'adaptateur sur le RecyclerView
                ingredientsRecyclerView.setAdapter(ingredientAdapter);

                // Afficher les ingrédients déjà dans le ViewModel s'il y en a
                List<Ingredient> current = sharedStepsViewModel._ingredients.getValue();
                if (current != null && !current.isEmpty()) {
                    Log.d(TAG, "Affichage direct de " + current.size() + " ingrédients");
                    ingredientAdapter.setIngredientList(current);
                }
            }

            // Configurer les observateurs
            sharedStepsViewModel.getIngredients().observe(getViewLifecycleOwner(), ingredients -> {
                Log.d(TAG, "Mise à jour des ingrédients observée: " +
                        (ingredients != null ? ingredients.size() : 0) + " ingrédients");

                if (ingredientAdapter != null && ingredients != null) {
                    ingredientAdapter.setIngredientList(ingredients);
                }
            });

            // Définir le bouton pour passer aux étapes
            if (startCookingButton != null) {
                startCookingButton.setOnClickListener(v -> loadStepsFragment());
            }

            // Mettre à jour le titre et sous-titre
            updateTitleAndSubtitle();

            // Mettre à jour les informations nutritionnelles
            updateNutritionInfo();

            // Mettre à jour le nombre de portions
            if (servingsTextView != null) {
                int participantCount = sharedStepsViewModel.getParticipantCount();
                servingsTextView.setText(String.valueOf(participantCount));
            }

            // Force l'affichage de l'interface
            rootView.post(() -> {
                updateUI();
                if (ingredientAdapter != null) {
                    ingredientAdapter.notifyDataSetChanged();
                }
            });

            return rootView;
        } catch (Exception e) {
            Log.e(TAG, "Exception dans onCreateView", e);
            return inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
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
            if (getContext() != null && ingredientsRecyclerView != null) {
                Log.d(TAG, "Configuration du RecyclerView");

                // Vérifier que le RecyclerView est visible
                if (ingredientsRecyclerView.getVisibility() != View.VISIBLE) {
                    Log.d(TAG, "RecyclerView n'est pas visible, définition sur VISIBLE");
                    ingredientsRecyclerView.setVisibility(View.VISIBLE);
                }

                // Création de l'adaptateur
                ingredientAdapter = new IngredientAdapter(getContext(), this);

                // Configuration du LayoutManager
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                ingredientsRecyclerView.setLayoutManager(layoutManager);

                // Ajouter des informations de débogage sur le LayoutManager
                Log.d(TAG, "LayoutManager configuré: " + layoutManager.getClass().getSimpleName());

                // Définir un ItemDecoration pour l'espacement
                ingredientsRecyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                        getContext(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));

                // Désactiver le changement d'animation pour éviter les problèmes de recyclage
                ingredientsRecyclerView.setItemAnimator(null);

                // Définir l'adaptateur
                ingredientsRecyclerView.setAdapter(ingredientAdapter);
                Log.d(TAG, "Adaptateur défini pour le RecyclerView");

                // Vérifier l'état du RecyclerView après configuration
                Log.d(TAG, "État du RecyclerView après configuration - " +
                        "Adapter: " + (ingredientsRecyclerView.getAdapter() != null) +
                        ", LayoutManager: " + (ingredientsRecyclerView.getLayoutManager() != null));

                // Forcer les ingrédients initiaux s'ils sont déjà disponibles
                List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();
                if (ingredients != null && !ingredients.isEmpty()) {
                    Log.d(TAG, "Chargement initial de " + ingredients.size() + " ingrédients");

                    // Log détaillé pour chaque ingrédient
                    for (int i = 0; i < ingredients.size(); i++) {
                        Ingredient ing = ingredients.get(i);
                        Log.d(TAG, "Ingrédient " + (i+1) + ": " + ing.getName() +
                                " (" + ing.getQuantity() + " " + ing.getUnit() + ")");
                    }

                    ingredientAdapter.setIngredientList(ingredients);

                    // Forcer un rafraîchissement
                    ingredientAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Aucun ingrédient initial disponible");
                }

                // Vérifier l'état final du RecyclerView
                if (ingredientsRecyclerView.getAdapter() != null) {
                    Log.d(TAG, "RecyclerView configuré avec " +
                            ingredientsRecyclerView.getAdapter().getItemCount() + " éléments");
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

                        // Logs détaillés pour chaque ingrédient
                        for (int i = 0; i < ingredients.size(); i++) {
                            Ingredient ing = ingredients.get(i);
                            Log.d(TAG, "Ingrédient " + (i+1) + ": " + ing.getName() +
                                    " (" + ing.getQuantity() + " " + ing.getUnit() + ")");
                        }

                        // Mettre à jour l'adaptateur
                        ingredientAdapter.setIngredientList(ingredients);

                        // Vérifier si le RecyclerView est visible et configuré correctement
                        if (ingredientsRecyclerView != null) {
                            Log.d(TAG, "État du RecyclerView - Visible: " +
                                    (ingredientsRecyclerView.getVisibility() == View.VISIBLE) +
                                    ", Adapter: " + (ingredientsRecyclerView.getAdapter() != null) +
                                    ", ItemCount: " + (ingredientsRecyclerView.getAdapter() != null ?
                                    ingredientsRecyclerView.getAdapter().getItemCount() : 0));

                            // Forcer un rafraîchissement du RecyclerView
                            ingredientsRecyclerView.post(() -> {
                                if (ingredientAdapter != null) {
                                    Log.d(TAG, "Rafraîchissement forcé de l'adaptateur");
                                    ingredientAdapter.notifyDataSetChanged();
                                }
                            });
                        }

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
            int duration = sharedStepsViewModel.getTotalDuration();
            recipeSubtitleTextView.setText(mealType + " / " + duration + " min");
            Log.d(TAG, "Sous-titre mis à jour: " + mealType + " / " + duration + " min");

            // Récupérer les valeurs nutritionnelles depuis le ViewModel
            Log.d(TAG, "Récupération des informations nutritionnelles du ViewModel:");
            int calories = sharedStepsViewModel.getCalories();
            int protein = sharedStepsViewModel.getProtein();
            int carbs = sharedStepsViewModel.getCarbs();
            int fat = sharedStepsViewModel.getFat();
            Log.d(TAG, "Valeurs du ViewModel - calories: " + calories + ", protéines: " + protein +
                    "g, glucides: " + carbs + "g, graisses: " + fat + "g");

            // Mettre à jour les informations nutritionnelles
            // Afficher les calories
            if (calories > 0) {
                caloriesValueTextView.setText(calories + " kcal");
                Log.d(TAG, "Affichage calories depuis API: " + calories + " kcal");
            } else {
                // Si pas de calories définies, calculer à partir des ingrédients
                List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();
                int totalCalories = 0;

                if (ingredients != null && !ingredients.isEmpty()) {
                    // Calculer les calories totales
                    for (Ingredient ingredient : ingredients) {
                        totalCalories += ingredient.getCalories();
                    }
                }

                // Afficher les calories totales
                caloriesValueTextView.setText(totalCalories + " kcal");
                Log.d(TAG, "Calories calculées depuis les ingrédients: " + totalCalories + " kcal");
            }

            // Afficher les protéines
            if (protein > 0) {
                proteinsValueTextView.setText(protein + "g");
                Log.d(TAG, "Affichage protéines depuis API: " + protein + "g");
            } else {
                proteinsValueTextView.setText("0g");
                Log.d(TAG, "Aucune protéine trouvée, affichage de 0g");
            }

            // Afficher les glucides
            if (carbs > 0) {
                carbsValueTextView.setText(carbs + "g");
                Log.d(TAG, "Affichage glucides depuis API: " + carbs + "g");
            } else {
                carbsValueTextView.setText("0g");
                Log.d(TAG, "Aucun glucide trouvé, affichage de 0g");
            }

            // Afficher les graisses
            if (fat > 0) {
                fatValueTextView.setText(fat + "g");
                Log.d(TAG, "Affichage graisses depuis API: " + fat + "g");
            } else {
                fatValueTextView.setText("0g");
                Log.d(TAG, "Aucune graisse trouvée, affichage de 0g");
            }

            Log.d(TAG, "Informations nutritionnelles mises à jour et affichées: " +
                    calories + " kcal, " + protein + "g protein, " +
                    carbs + "g carbs, " + fat + "g fat");

            // Mettre à jour le nombre de portions
            servings = sharedStepsViewModel.getParticipantCount();
            if (servings <= 0) servings = 2; // Valeur par défaut si non définie
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

    private void updateTitleAndSubtitle() {
        try {
            if (recipeTitleTextView != null && recipeSubtitleTextView != null) {
                recipeTitleTextView.setText(sharedStepsViewModel.getMenuName());
                recipeSubtitleTextView.setText(mealType + " / " + sharedStepsViewModel.getTotalDuration() + " min");
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

    private void loadStepsFragment() {
        try {
            if (getActivity() != null) {
                RecipeStepsFragment stepsFragment = RecipeStepsFragment.newInstance();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipe_fragment_container, stepsFragment)
                        .addToBackStack(null)
                        .commit();

                Log.d(TAG, "Navigation vers RecipeStepsFragment réussie");
            } else {
                Log.e(TAG, "getActivity() est null dans loadStepsFragment");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadStepsFragment", e);
        }
    }
}