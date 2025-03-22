package com.gfaim.activities.recipe.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gfaim.R;
import com.gfaim.activities.calendar.SharedStepsViewModel;
import com.gfaim.activities.calendar.model.Ingredient;
import com.gfaim.activities.calendar.model.Recipe;
import com.gfaim.activities.recipe.fragments.RecipeIngredientsFragment;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private static final String TAG = "RecipeActivity";
    private SharedStepsViewModel sharedStepsViewModel;
    private ImageView backButton;

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

            // Ajouter des ingrédients de test UNIQUEMENT si aucun ingrédient n'est disponible
            // et si aucun menuName n'est défini (signifie qu'aucune recette réelle n'a été sélectionnée)
            if ((sharedStepsViewModel.getIngredients().getValue() == null ||
                    sharedStepsViewModel.getIngredients().getValue().isEmpty()) &&
                    (sharedStepsViewModel.getMenuName() == null ||
                            sharedStepsViewModel.getMenuName().isEmpty())) {
                ensureTestIngredientsAvailable();
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
                // Récupérer le nom du menu s'il existe
                if (getIntent().hasExtra("menuName")) {
                    String menuName = getIntent().getStringExtra("menuName");
                    if (menuName != null && !menuName.isEmpty()) {
                        sharedStepsViewModel.setMenuName(menuName);
                        Log.d(TAG, "Menu name set from intent: " + menuName);

                        // Essayer de récupérer la recette complète associée au menuName
                        loadRecipeDataByMenuName(menuName);
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