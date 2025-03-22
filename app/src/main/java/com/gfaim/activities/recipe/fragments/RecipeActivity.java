package com.gfaim.activities.recipe.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.activities.calendar.model.Ingredient;
import com.gfaim.activities.calendar.model.Recipe;
import com.gfaim.activities.calendar.model.Step;
import com.gfaim.activities.calendar.model.StepIngredient;
import com.gfaim.api.ApiClient;
import com.gfaim.api.RecipeService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeActivity extends AppCompatActivity {

    private static final String TAG = "RecipeActivity";
    private SharedStepsViewModel sharedStepsViewModel;
    private ImageView backButton;
    private Long recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.recipe);

            // Initialiser le ViewModel
            sharedStepsViewModel = new ViewModelProvider(this).get(SharedStepsViewModel.class);

            // Initialiser les vues
            initViews();

            // Important: Réinitialiser le ViewModel pour éviter des données d'une recette précédente
            sharedStepsViewModel.reset();

            // Vérifier si nous avons des données depuis l'intent
            handleIntent();

            // Si nous avons l'ID de la recette, la charger depuis l'API
            if (recipeId != null) {
                loadRecipeFromApi(recipeId);
            }
            // Sinon, essayer les autres méthodes de chargement
            else {
                // Ajouter des ingrédients de test UNIQUEMENT si aucun ingrédient n'est disponible
                // et si aucun menuName n'est défini (signifie qu'aucune recette réelle n'a été sélectionnée)
                if ((sharedStepsViewModel.getIngredients().getValue() == null ||
                        sharedStepsViewModel.getIngredients().getValue().isEmpty()) &&
                        (sharedStepsViewModel.getMenuName() == null ||
                                sharedStepsViewModel.getMenuName().isEmpty())) {
                    ensureTestIngredientsAvailable();
                }
            }

            // Charger le fragment d'ingrédients par défaut
            if (savedInstanceState == null) {
                loadIngredientsFragment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la création de l'activité", e);
        }
    }

    private void initViews() {
        try {
            backButton = findViewById(R.id.back);
            if (backButton != null) {
                backButton.setOnClickListener(v -> onBackPressed());
            } else {
                Log.e(TAG, "Bouton de retour est null dans initViews");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans initViews", e);
        }
    }

    private void handleIntent() {
        try {
            if (getIntent() != null && sharedStepsViewModel != null) {
                // Récupérer l'ID de la recette s'il existe
                if (getIntent().hasExtra("recipe_id")) {
                    recipeId = getIntent().getLongExtra("recipe_id", -1);
                    if (recipeId <= 0) {
                        recipeId = null;
                        Log.e(TAG, "ID de recette invalide dans l'intent");
                    } else {
                        Log.d(TAG, "ID de recette récupéré: " + recipeId);
                    }
                }

                // Récupérer le nom du menu s'il existe
                if (getIntent().hasExtra("menuName")) {
                    String menuName = getIntent().getStringExtra("menuName");
                    if (menuName != null && !menuName.isEmpty()) {
                        sharedStepsViewModel.setMenuName(menuName);
                        Log.d(TAG, "Menu name set from intent: " + menuName);

                        // Si pas d'ID de recette, essayer de récupérer la recette complète associée au menuName
                        if (recipeId == null) {
                            loadRecipeDataByMenuName(menuName);
                        }
                    }
                } else {
                    Log.d(TAG, "Pas de menuName dans l'intent");
                }

                // Charger les données transmises directement via l'intent
                loadDataFromIntent();
            } else {
                Log.d(TAG, "Intent ou ViewModel null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans handleIntent", e);
        }
    }

    /**
     * Charge les détails d'une recette depuis l'API
     * @param recipeId L'identifiant de la recette à charger
     */
    private void loadRecipeFromApi(Long recipeId) {
        try {
            Log.d(TAG, "Chargement de la recette depuis l'API, ID: " + recipeId);

            RecipeService recipeService = ApiClient.getClient(this).create(RecipeService.class);
            Call<Recipe> call = recipeService.getRecipe(recipeId);

            call.enqueue(new Callback<Recipe>() {
                @Override
                public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Recipe recipe = response.body();

                        // Afficher les données brutes et le type de chaque champ pour le débogage
                        Log.d(TAG, "Réponse API brute: " + recipe.toString());
                        Log.d(TAG, "---- Détails de la recette reçue ----");
                        Log.d(TAG, "ID: " + recipe.getId());
                        Log.d(TAG, "Nom: " + recipe.getName());

                        // Log des valeurs nutritionnelles avec leur type
                        Log.d(TAG, "Calories (brut): " + recipe.getCalories() + " (type: " +
                                (recipe.getCalories() != 0 ? "valeur présente" : "valeur absente") + ")");
                        Log.d(TAG, "Protéines (brut): " + recipe.getProtein() + "g (type: " +
                                (recipe.getProtein() != 0 ? "valeur présente" : "valeur absente") + ")");
                        Log.d(TAG, "Glucides (brut): " + recipe.getCarbs() + "g (type: " +
                                (recipe.getCarbs() != 0 ? "valeur présente" : "valeur absente") + ")");
                        Log.d(TAG, "Graisses (brut): " + recipe.getFat() + "g (type: " +
                                (recipe.getFat() != 0 ? "valeur présente" : "valeur absente") + ")");

                        Log.d(TAG, "Prêt en: " + recipe.getReadyInMinutes() + " min");
                        Log.d(TAG, "Portions: " + recipe.getNbServings());

                        // Étapes
                        if (recipe.getSteps() != null) {
                            Log.d(TAG, "Nombre d'étapes: " + recipe.getSteps().size());
                            for (int i = 0; i < recipe.getSteps().size(); i++) {
                                Step step = recipe.getSteps().get(i);
                                Log.d(TAG, "Étape " + (i+1) + ": " + step.getDescription());
                            }
                        } else {
                            Log.d(TAG, "Aucune étape trouvée dans la recette");
                        }

                        updateViewModelWithRecipe(recipe);
                        Log.d(TAG, "Recette chargée avec succès depuis l'API: " + recipe.getName());
                    } else {
                        Log.e(TAG, "Erreur lors du chargement de la recette: " +
                                (response.errorBody() != null ? response.errorBody().toString() : "Erreur inconnue") +
                                ", code: " + response.code());

                        // Tenter d'afficher plus de détails sur l'erreur
                        if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Détail de l'erreur: " + response.errorBody().string());
                            } catch (Exception e) {
                                Log.e(TAG, "Impossible de lire le corps de l'erreur", e);
                            }
                        }

                        Toast.makeText(RecipeActivity.this,
                                "Erreur lors du chargement de la recette: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                        // Charger des données de test en cas d'échec
                        ensureTestIngredientsAvailable();
                    }

                    // Mettre à jour l'interface utilisateur
                    loadIngredientsFragment();
                }

                @Override
                public void onFailure(Call<Recipe> call, Throwable t) {
                    Log.e(TAG, "Échec de la requête API", t);
                    Toast.makeText(RecipeActivity.this,
                            "Échec de la connexion à l'API: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    // Charger des données de test en cas d'échec
                    ensureTestIngredientsAvailable();
                    loadIngredientsFragment();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception lors du chargement de la recette depuis l'API", e);
            ensureTestIngredientsAvailable();
            loadIngredientsFragment();
        }
    }

    /**
     * Met à jour le ViewModel avec les données de la recette reçue
     * @param recipe La recette reçue de l'API
     */
    private void updateViewModelWithRecipe(Recipe recipe) {
        try {
            // Vérifier si la recette est valide
            if (recipe == null) {
                Log.e(TAG, "La recette est null dans updateViewModelWithRecipe");
                return;
            }

            Log.d(TAG, "Début de l'extraction des données de la recette: " + recipe.getName());

            // Mettre à jour le nom de la recette
            sharedStepsViewModel.setMenuName(recipe.getName());

            // Mettre à jour le nombre de portions
            int servings = recipe.getNbServings();
            sharedStepsViewModel.setParticipantCount(servings > 0 ? servings : 2); // Valeur par défaut = 2

            // APPROCHE SIMPLIFIÉE:
            // 1. Créer une liste temporaire d'ingrédients
            List<Ingredient> tempIngredients = new ArrayList<>();

            // 2. Extraire les ingrédients de chaque étape
            if (recipe.getSteps() != null) {
                Log.d(TAG, "La recette contient " + recipe.getSteps().size() + " étapes");

                for (Step step : recipe.getSteps()) {
                    if (step.getIngredients() != null) {
                        for (StepIngredient si : step.getIngredients()) {
                            if (si != null && si.getIngredientCatalog() != null) {
                                // Extraire le nom (préférer le nom français)
                                String name = si.getIngredientCatalog().getNameFr();
                                if (name == null || name.isEmpty()) {
                                    name = si.getIngredientCatalog().getName();
                                }

                                // Extraire l'unité (préférer le nom français)
                                String unit = "";
                                if (si.getUnit() != null) {
                                    unit = si.getUnit().getNameFr();
                                    if (unit == null || unit.isEmpty()) {
                                        unit = si.getUnit().getNameEn();
                                    }
                                }

                                // Créer et ajouter l'ingrédient
                                tempIngredients.add(new Ingredient(
                                        name,
                                        0, // calories à 0 par défaut
                                        si.getQuantity(),
                                        unit
                                ));

                                Log.d(TAG, "Ajout ingrédient: " + name);
                            }
                        }
                    }
                }

                // 3. Mettre à jour directement la liste d'ingrédients du ViewModel
                if (!tempIngredients.isEmpty()) {
                    // SIMPLIFICATION: on va directement modifier la liste dans le LiveData
                    List<Ingredient> currentList = new ArrayList<>(tempIngredients);
                    sharedStepsViewModel._ingredients.setValue(currentList);

                    Log.d(TAG, "Nombre d'ingrédients ajoutés directement: " + currentList.size());
                } else {
                    Log.w(TAG, "Aucun ingrédient trouvé");
                }

                // Mise à jour des étapes et durées
                List<String> stepDescriptions = new ArrayList<>();
                List<Integer> stepDurations = new ArrayList<>();

                for (Step step : recipe.getSteps()) {
                    stepDescriptions.add(step.getDescription());
                    stepDurations.add(5); // 5 minutes par défaut
                }

                // Mise à jour des étapes
                sharedStepsViewModel.setRawSteps(recipe.getSteps());
                sharedStepsViewModel.setSteps(stepDescriptions);
                sharedStepsViewModel.setDurations(stepDurations);
            }

            // Mettre à jour les informations nutritionnelles
            sharedStepsViewModel.setNutritionInfo(
                    recipe.getCalories(),
                    recipe.getProtein(),
                    recipe.getCarbs(),
                    recipe.getFat()
            );

            Log.d(TAG, "ViewModel mis à jour avec les données de la recette: " + recipe.getName());
        } catch (Exception e) {
            Log.e(TAG, "Exception dans updateViewModelWithRecipe", e);
            e.printStackTrace();
        }
    }

    /**
     * Charge les données d'une recette à partir de l'intent
     */
    private void loadDataFromIntent() {
        try {
            if (getIntent() != null) {
                // Vérifier si des ingrédients sont transmis via l'intent
                if (getIntent().hasExtra("ingredients")) {
                    ArrayList<Parcelable> ingredients = getIntent().getParcelableArrayListExtra("ingredients");
                    if (ingredients != null && !ingredients.isEmpty()) {
                        // Mettre à jour le ViewModel avec ces ingrédients
                        for (Parcelable ingredient : ingredients) {
                            sharedStepsViewModel.addIngredient((Ingredient) ingredient);
                        }
                        Log.d(TAG, "Ingrédients chargés depuis l'intent: " + ingredients.size());
                    }
                }

                // Vérifier si des étapes sont transmises via l'intent
                if (getIntent().hasExtra("steps")) {
                    ArrayList<String> steps = getIntent().getStringArrayListExtra("steps");
                    if (steps != null && !steps.isEmpty()) {
                        sharedStepsViewModel.setSteps(steps);
                        Log.d(TAG, "Étapes chargées depuis l'intent: " + steps.size());
                    }
                }

                // Vérifier si des durées sont transmises via l'intent
                if (getIntent().hasExtra("durations")) {
                    ArrayList<Integer> durations = getIntent().getIntegerArrayListExtra("durations");
                    if (durations != null && !durations.isEmpty()) {
                        sharedStepsViewModel.setDurations(durations);
                        Log.d(TAG, "Durées chargées depuis l'intent: " + durations.size());
                    }
                }

                // Vérifier si une durée totale est transmise
                if (getIntent().hasExtra("duration")) {
                    int duration = getIntent().getIntExtra("duration", 0);
                    if (duration > 0) {
                        // Si pas de durées par étape déjà définies, on utilise la durée totale
                        if (sharedStepsViewModel.getDurations().getValue() == null ||
                                sharedStepsViewModel.getDurations().getValue().isEmpty()) {
                            List<Integer> singleDuration = new ArrayList<>();
                            singleDuration.add(duration);
                            sharedStepsViewModel.setDurations(singleDuration);
                            Log.d(TAG, "Durée totale chargée depuis l'intent: " + duration + " min");
                        }
                    }
                }

                // Vérifier si un nombre de participants est transmis
                if (getIntent().hasExtra("participantCount")) {
                    int participantCount = getIntent().getIntExtra("participantCount", 2);
                    sharedStepsViewModel.setParticipantCount(participantCount);
                    Log.d(TAG, "Nombre de participants: " + participantCount);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadDataFromIntent", e);
        }
    }

    /**
     * Recherche et charge les données d'une recette à partir de son nom
     * Cette méthode simule la recherche dans une base de données
     * Dans une vraie application, vous utiliseriez une requête à votre source de données
     */
    private void loadRecipeDataByMenuName(String menuName) {
        try {
            Log.d(TAG, "Recherche de la recette: " + menuName);

            // Dans une vraie application, vous feriez une requête à votre base de données
            // ou à votre service web pour récupérer la recette complète

            // Pour l'instant, essayons de récupérer la recette depuis le fragment Calendar
            // en utilisant le nom du menu

            // Vérifier d'abord si nous avons déjà des données dans le ViewModel
            if (sharedStepsViewModel.getIngredients().getValue() != null &&
                    !sharedStepsViewModel.getIngredients().getValue().isEmpty()) {
                Log.d(TAG, "Des ingrédients existent déjà dans le ViewModel, pas besoin de les charger");
                return;
            }

            // Sinon, continuer avec les données de test associées au menuName
            if ("Ramen".equalsIgnoreCase(menuName)) {
                loadRamenRecipe();
            } else if ("Pasta".equalsIgnoreCase(menuName)) {
                loadPastaRecipe();
            } else if ("Chicken Curry".equalsIgnoreCase(menuName)) {
                loadChickenCurryRecipe();
            } else {
                Log.d(TAG, "Aucune recette prédéfinie pour: " + menuName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadRecipeDataByMenuName", e);
        }
    }

    /**
     * Charge une recette de Ramen prédéfinie
     */
    private void loadRamenRecipe() {
        try {
            // Créer une liste d'ingrédients pour le Ramen
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient("Noodles", 220));
            ingredients.add(new Ingredient("Chicken breast", 150));
            ingredients.add(new Ingredient("Soy sauce", 15));
            ingredients.add(new Ingredient("Miso paste", 50));
            ingredients.add(new Ingredient("Green onions", 10));
            ingredients.add(new Ingredient("Soft-boiled egg", 70));

            // Mettre à jour le ViewModel avec ces ingrédients
            for (Ingredient ingredient : ingredients) {
                sharedStepsViewModel.addIngredient(ingredient);
            }

            // Définir les étapes
            List<String> steps = new ArrayList<>();
            steps.add("Boil water and cook noodles according to package instructions.");
            steps.add("In a separate pot, combine broth ingredients and bring to a simmer.");
            steps.add("Slice chicken and cook in the broth until done.");
            steps.add("Serve noodles in bowls, pour broth over, and top with chicken and garnishes.");

            // Définir les durées
            List<Integer> durations = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                durations.add(5); // 5 minutes par étape
            }

            // Mettre à jour le ViewModel
            sharedStepsViewModel.setSteps(steps);
            sharedStepsViewModel.setDurations(durations);
            sharedStepsViewModel.setParticipantCount(2);

            Log.d(TAG, "Recette Ramen chargée: " + ingredients.size() + " ingrédients, " + steps.size() + " étapes");
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadRamenRecipe", e);
        }
    }

    /**
     * Charge une recette de Pasta prédéfinie
     */
    private void loadPastaRecipe() {
        try {
            // Créer une liste d'ingrédients pour les pâtes
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient("Spaghetti", 180));
            ingredients.add(new Ingredient("Ground beef", 200));
            ingredients.add(new Ingredient("Tomato sauce", 100));
            ingredients.add(new Ingredient("Onion", 40));
            ingredients.add(new Ingredient("Garlic", 15));
            ingredients.add(new Ingredient("Parmesan cheese", 30));

            // Mettre à jour le ViewModel avec ces ingrédients
            for (Ingredient ingredient : ingredients) {
                sharedStepsViewModel.addIngredient(ingredient);
            }

            // Définir les étapes
            List<String> steps = new ArrayList<>();
            steps.add("Boil water and cook pasta according to package instructions.");
            steps.add("In a pan, sauté onions and garlic until fragrant.");
            steps.add("Add ground beef and cook until browned.");
            steps.add("Add tomato sauce and simmer for 10 minutes.");
            steps.add("Serve pasta topped with sauce and sprinkle with cheese.");

            // Définir les durées
            List<Integer> durations = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                durations.add(5); // 5 minutes par étape
            }

            // Mettre à jour le ViewModel
            sharedStepsViewModel.setSteps(steps);
            sharedStepsViewModel.setDurations(durations);
            sharedStepsViewModel.setParticipantCount(4);

            Log.d(TAG, "Recette Pasta chargée: " + ingredients.size() + " ingrédients, " + steps.size() + " étapes");
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadPastaRecipe", e);
        }
    }

    /**
     * Charge une recette de Poulet au curry prédéfinie
     */
    private void loadChickenCurryRecipe() {
        try {
            // Créer une liste d'ingrédients
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient("Chicken thighs", 300));
            ingredients.add(new Ingredient("Curry powder", 20));
            ingredients.add(new Ingredient("Coconut milk", 150));
            ingredients.add(new Ingredient("Onion", 40));
            ingredients.add(new Ingredient("Garlic", 15));
            ingredients.add(new Ingredient("Ginger", 10));
            ingredients.add(new Ingredient("Rice", 200));

            // Mettre à jour le ViewModel avec ces ingrédients
            for (Ingredient ingredient : ingredients) {
                sharedStepsViewModel.addIngredient(ingredient);
            }

            // Définir les étapes
            List<String> steps = new ArrayList<>();
            steps.add("Cut chicken into bite-sized pieces.");
            steps.add("In a large pot, sauté onions, garlic, and ginger until fragrant.");
            steps.add("Add chicken and cook until browned on all sides.");
            steps.add("Add curry powder and stir to coat the chicken.");
            steps.add("Pour in coconut milk and simmer for 20 minutes.");
            steps.add("Serve hot over cooked rice.");

            // Définir les durées
            List<Integer> durations = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                durations.add(5); // 5 minutes par étape
            }

            // Mettre à jour le ViewModel
            sharedStepsViewModel.setSteps(steps);
            sharedStepsViewModel.setDurations(durations);
            sharedStepsViewModel.setParticipantCount(3);

            Log.d(TAG, "Recette Chicken Curry chargée: " + ingredients.size() + " ingrédients, " + steps.size() + " étapes");
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadChickenCurryRecipe", e);
        }
    }

    /**
     * Ajoute des ingrédients de test si aucun ingrédient n'existe déjà
     * Cette méthode est utile pour tester l'affichage
     */
    private void ensureTestIngredientsAvailable() {
        try {
            if (sharedStepsViewModel != null) {
                List<Ingredient> ingredients = sharedStepsViewModel.getIngredients().getValue();

                // Si la liste d'ingrédients est vide, ajouter des ingrédients de test
                if (ingredients == null || ingredients.isEmpty()) {
                    Log.d(TAG, "Ajout d'ingrédients de test");

                    // Créer une liste temporaire pour éviter les modifications concurrentes
                    List<Ingredient> testIngredients = new ArrayList<>();

                    // Ajouter les ingrédients de test à la liste temporaire
                    testIngredients.add(new Ingredient("Chicken breasts", 150));
                    testIngredients.add(new Ingredient("Unsalted butter", 80));
                    testIngredients.add(new Ingredient("Sesame oil", 120));
                    testIngredients.add(new Ingredient("Fresh ginger", 30));
                    testIngredients.add(new Ingredient("Large eggs", 70));
                    testIngredients.add(new Ingredient("Noodles", 220));
                    testIngredients.add(new Ingredient("Soy sauce", 15));

                    // Définir les ingrédients de test
                    for (Ingredient ingredient : testIngredients) {
                        sharedStepsViewModel.addIngredient(ingredient);
                    }

                    // Assurer qu'un nom de menu est défini si ce n'est pas déjà le cas
                    if (sharedStepsViewModel.getMenuName() == null || sharedStepsViewModel.getMenuName().isEmpty()) {
                        sharedStepsViewModel.setMenuName("Test Recipe");
                    }

                    // Définir un nombre de participants par défaut
                    if (sharedStepsViewModel.getParticipantCount() <= 0) {
                        sharedStepsViewModel.setParticipantCount(2);
                    }

                    Log.d(TAG, "Ingrédients de test ajoutés: " + testIngredients.size());
                } else {
                    Log.d(TAG, "Ingrédients existants trouvés: " + ingredients.size());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans ensureTestIngredientsAvailable", e);
        }
    }

    private void loadIngredientsFragment() {
        try {
            if (isDestroyed() || isFinishing()) {
                Log.e(TAG, "Activité détruite ou en cours de fermeture, impossible de charger le fragment");
                return;
            }

            // Récupérer le mealType depuis l'intent s'il existe
            String mealType = "Repas";
            if (getIntent().hasExtra("mealType")) {
                mealType = getIntent().getStringExtra("mealType");
                Log.d(TAG, "MealType récupéré de l'intent: " + mealType);
            }

            // Créer le fragment avec le mealType
            RecipeIngredientsFragment fragment = RecipeIngredientsFragment.newInstance(mealType);            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.recipe_fragment_container, fragment)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadIngredientsFragment", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            // Si c'est le dernier fragment dans la pile, quitter l'activité
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                // Sinon, revenir au fragment précédent
                getSupportFragmentManager().popBackStack();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans onBackPressed", e);
            super.onBackPressed(); // En cas d'erreur, on utilise le comportement par défaut
        }
    }
}