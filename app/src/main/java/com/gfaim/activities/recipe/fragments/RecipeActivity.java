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
import com.gfaim.activities.calendar.model.Recipe;
import com.gfaim.activities.calendar.model.Step;
import com.gfaim.activities.calendar.model.StepIngredient;
import com.gfaim.api.ApiClient;
import com.gfaim.api.RecipeService;
import com.gfaim.activities.NavigationBar;
import com.gfaim.models.CreateMealBody;
import com.gfaim.models.CreateRecipeBody;
import com.gfaim.models.CreateRecipeStepIngrBody;
import com.gfaim.models.FoodItem;
import com.gfaim.models.RecipeResponseBody;
import com.gfaim.models.RecipeStep;
import com.gfaim.models.RecipeStepIngrResponse;
import com.gfaim.models.RecipeStepIngredientResponse;
import com.gfaim.models.RecipeStepResponse;

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

    List<FoodItem> ingredients = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.recipe);

            // Initialiser le ViewModel
            sharedStepsViewModel = new ViewModelProvider(this).get(SharedStepsViewModel.class);

            // Initialiser les vues
            initViews();

            // Initialiser la barre de navigation avec le bouton Recipes actif
            NavigationBar navigationBar = new NavigationBar(this);
            navigationBar.setActiveButton(R.id.btn_recipes);

            // Important: Réinitialiser le ViewModel pour éviter des données d'une recette précédente
            sharedStepsViewModel.reset();

            // Vérifier si nous avons des données depuis l'intent
            handleIntent();

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
                    // ensureTestIngredientsAvailable();
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
                if (getIntent().hasExtra("id")) {
                    recipeId = getIntent().getLongExtra("id", -1);
                    if (recipeId <= 0) {
                        recipeId = null;
                        Log.e(TAG, "ID de recette invalide dans l'intent");
                    } else {
                        Log.d(TAG, "ID de recette récupéré: " + recipeId);
                    }
                }

                // Récupérer le type de repas spécifique s'il existe (textTitle du card_meal)
                if (getIntent().hasExtra("mealTitle")) {
                    String mealTitle = getIntent().getStringExtra("mealTitle");
                    Log.d(TAG, "Type de repas spécifique récupéré: " + mealTitle);
                }
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
            Call<RecipeResponseBody> call = recipeService.getRecipe(recipeId);

            call.enqueue(new Callback<RecipeResponseBody>() {
                @Override
                public void onResponse(Call<RecipeResponseBody> call, Response<RecipeResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RecipeResponseBody recipe = response.body();

                        Log.d(TAG, "Réponse API brute: " + recipe.toString());
                        Log.d(TAG, "---- Détails de la recette reçue ----");
                        Log.d(TAG, "ID: " + recipe.getId());
                        Log.d(TAG, "Nom: " + recipe.getName());

                        Log.d(TAG, "Calories (brut): " + recipe.getCalories() + " (type: " +
                                (recipe.getCalories() != null ? "valeur présente" : "valeur absente") + ")");
                        Log.d(TAG, "Protéines (brut): " + recipe.getProtein() + "g (type: " +
                                (recipe.getProtein() != null ? "valeur présente" : "valeur absente") + ")");
                        Log.d(TAG, "Glucides (brut): " + recipe.getCarbs() + "g (type: " +
                                (recipe.getCarbs() != null ? "valeur présente" : "valeur absente") + ")");
                        Log.d(TAG, "Graisses (brut): " + recipe.getFat() + "g (type: " +
                                (recipe.getFat() != null ? "valeur présente" : "valeur absente") + ")");

                        Log.d(TAG, "Prêt en: " + recipe.getReadyInMinutes() + " min");
                        Log.d(TAG, "Portions: " + recipe.getNbServings());

                        // Étapes
                        if (recipe.getSteps() != null) {
                            Log.d(TAG, "Nombre d'étapes: " + recipe.getSteps().size());
                            for (int i = 0; i < recipe.getSteps().size(); i++) {
                                RecipeStepResponse step = recipe.getSteps().get(i);
                                System.out.println("steps" + step);
                                    for(RecipeStepIngrResponse ingr :step.getIngredients()) {
                                        System.out.println("ingr" + ingr);
                                        ingredients.add(new FoodItem(ingr.getIngredientCatalog().getNameFr(), ingr.getIngredientCatalog().getNameEn(), ingr.getIngredientCatalog().getName(),ingr.getIngredientCatalog().getId() ));
                                }
                                System.out.println("ingredients " + ingredients);

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
                        //ensureTestIngredientsAvailable();
                    }

                    // Mettre à jour l'interface utilisateur
                    loadIngredientsFragment();
                }

                @Override
                public void onFailure(Call<RecipeResponseBody> call, Throwable t) {
                    Log.e(TAG, "Échec de la requête API", t);
                    Toast.makeText(RecipeActivity.this,
                            "Échec de la connexion à l'API: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    // Charger des données de test en cas d'échec
                    // ensureTestIngredientsAvailable();
                    loadIngredientsFragment();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception lors du chargement de la recette depuis l'API", e);
            //ensureTestIngredientsAvailable();
            loadIngredientsFragment();
        }
    }

    /**
     * Met à jour le ViewModel avec les données de la recette reçue
     * @param recipe La recette reçue de l'API
     */
    private void updateViewModelWithRecipe(RecipeResponseBody recipe) {
        if (recipe == null) {
            return;
        }

        // Mettre à jour le nom de la recette
        sharedStepsViewModel.setMenuName(recipe.getName());

        // Mettre à jour le nombre de portions
        sharedStepsViewModel.setParticipantCount(recipe.getNbServings());
        Log.d(TAG, "Nombre de portions mis à jour: " + recipe.getNbServings());

        // Mettre à jour la liste d'ingrédients dans le ViewModel
        sharedStepsViewModel._ingredients.setValue(ingredients);
        Log.d(TAG, "Nombre total d'ingrédients ajoutés: " + ingredients.size());

        // Mettre à jour les étapes
        sharedStepsViewModel.setRawSteps(recipe.getSteps());

        // Mettre à jour les descriptions d'étapes
        List<String> stepDescriptions = new ArrayList<>();
        for (RecipeStepResponse step : recipe.getSteps()) {
            stepDescriptions.add(step.getDescription());
        }
        sharedStepsViewModel.setSteps(stepDescriptions);

        // Mettre à jour la durée totale de préparation
        List<Integer> durations = new ArrayList<>();
        // Si readyInMinutes est disponible, utiliser cette valeur comme durée totale
        if (recipe.getReadyInMinutes() != null && recipe.getReadyInMinutes() > 0) {
            durations.add(recipe.getReadyInMinutes());
            Log.d(TAG, "Durée de préparation mise à jour: " + recipe.getReadyInMinutes() + " min");
        } else {
            // Sinon, utiliser une valeur par défaut
            durations.add(15); // 15 minutes par défaut si non spécifié
            Log.d(TAG, "Durée de préparation par défaut utilisée: 15 min");
        }
        sharedStepsViewModel.setDurations(durations);

        // Mettre à jour les informations nutritionnelles
        sharedStepsViewModel.setNutritionInfo(
                recipe.getCalories() != null ? recipe.getCalories() : 0,
                recipe.getProtein() != null ? recipe.getProtein() : 0,
                recipe.getCarbs() != null ? recipe.getCarbs() : 0,
                recipe.getFat() != null ? recipe.getFat() : 0
        );

        // Log des informations nutritionnelles
        Log.d(TAG, "Informations nutritionnelles mises à jour: " +
                "Calories: " + recipe.getCalories() +
                ", Protéines: " + recipe.getProtein() + "g" +
                ", Glucides: " + recipe.getCarbs() + "g" +
                ", Graisses: " + recipe.getFat() + "g");
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
        } catch (Exception e) {
            Log.e(TAG, "Exception dans loadRecipeDataByMenuName", e);
        }
    }
    /**
     * Ajoute des ingrédients de test si aucun ingrédient n'existe déjà
     * Cette méthode est utile pour tester l'affichage
     */
   /* private void ensureTestIngredientsAvailable() {
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
    }*/

    private void loadIngredientsFragment() {
        try {
            if (isDestroyed() || isFinishing()) {
                Log.e(TAG, "Activité détruite ou en cours de fermeture, impossible de charger le fragment");
                return;
            }

            // Récupérer le mealType depuis l'intent s'il existe
            String mealType = "Repas";
            if (getIntent().hasExtra("mealTitle")) {
                mealType = getIntent().getStringExtra("mealTitle");
                Log.d(TAG, "Type de repas spécifique utilisé pour le fragment: " + mealType);
            } else if (getIntent().hasExtra("mealType")) {
                mealType = getIntent().getStringExtra("mealType");
                Log.d(TAG, "MealType récupéré de l'intent: " + mealType);
            }

            // Créer le fragment avec le mealType
            RecipeIngredientsFragment fragment = RecipeIngredientsFragment.newInstance(mealType);
            getSupportFragmentManager()
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