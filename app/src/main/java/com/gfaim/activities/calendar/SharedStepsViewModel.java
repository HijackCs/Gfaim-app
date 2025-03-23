package com.gfaim.activities.calendar;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gfaim.activities.calendar.model.Ingredient;
import com.gfaim.activities.calendar.model.Step;
import com.gfaim.activities.calendar.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharedStepsViewModel extends ViewModel {
    private static final String TAG = "SharedStepsViewModel";

    private String menuName = "";
    private int participantCount = 1;

    // Rendre accessible le MutableLiveData
    public final MutableLiveData<List<Ingredient>> _ingredients = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Ingredient>> ingredients = _ingredients;

    private final MutableLiveData<List<String>> _steps = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> steps = _steps;

    // Stockage des étapes brutes avec leurs IDs
    private List<Step> rawSteps = new ArrayList<>();

    private final MutableLiveData<List<Integer>> _durations = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Integer>> durations = _durations;

    // Informations nutritionnelles
    private int calories = 0;
    private int protein = 0;
    private int carbs = 0;
    private int fat = 0;

    // Variable pour stocker la recette actuelle
    private Recipe currentRecipe = null;

    // Getter et Setter pour le nom du menu
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    // Getter et Setter pour le nombre de participants
    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    /**
     * Récupère le nombre de portions de la recette
     * @return Le nombre de portions
     */
    public int getNbServings() {
        return participantCount;
    }

    // Gestion des étapes brutes (avec leurs IDs)
    public List<Step> getRawSteps() {
        return rawSteps;
    }

    public void setRawSteps(List<Step> steps) {
        this.rawSteps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
    }

    // Gestion des étapes
    public LiveData<List<String>> getSteps() {
        return steps;
    }

    public void setSteps(List<String> newSteps) {
        _steps.setValue(new ArrayList<>(newSteps));
    }

    // Gestion des durées
    public LiveData<List<Integer>> getDurations() {
        return durations;
    }

    public void setDurations(List<Integer> newDurations) {
        _durations.setValue(new ArrayList<>(newDurations));
    }

    public int getTotalDuration() {
        List<Integer> currentDurations = _durations.getValue();
        if (currentDurations == null) return 0;
        return currentDurations.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Définit les informations nutritionnelles
     * @param calories Les calories
     * @param protein Les protéines en grammes
     * @param carbs Les glucides en grammes
     * @param fat Les graisses en grammes
     */
    public void setNutritionInfo(int calories, int protein, int carbs, int fat) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    /**
     * Récupère la valeur en calories
     * @return Les calories
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Récupère la valeur en protéines
     * @return Les protéines en grammes
     */
    public int getProtein() {
        return protein;
    }

    /**
     * Récupère la valeur en glucides
     * @return Les glucides en grammes
     */
    public int getCarbs() {
        return carbs;
    }

    /**
     * Récupère la valeur en graisses
     * @return Les graisses en grammes
     */
    public int getFat() {
        return fat;
    }

    /**
     * Réinitialise le ViewModel
     */
    public void reset() {
        // Réinitialiser le nom du menu
        menuName = "";

        // Réinitialiser le nombre de participants
        participantCount = 1;

        // Réinitialiser les informations nutritionnelles
        calories = 0;
        protein = 0;
        carbs = 0;
        fat = 0;

        // Réinitialiser les listes
        _ingredients.setValue(new ArrayList<>());
        _steps.setValue(new ArrayList<>());
        _durations.setValue(new ArrayList<>());
        rawSteps = new ArrayList<>();
    }

    /**
     * Méthode de débogage pour afficher l'état des valeurs nutritionnelles
     * @return Une chaîne formatée avec toutes les valeurs nutritionnelles
     */
    public String debugNutritionInfo() {
        return "Nutrition [calories=" + calories +
                ", protéines=" + protein + "g" +
                ", glucides=" + carbs + "g" +
                ", graisses=" + fat + "g]";
    }

    /**
     * Définit directement la liste complète d'ingrédients
     * @param ingredients La nouvelle liste d'ingrédients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        try {
            Log.d(TAG, "setIngredients appelé avec " + (ingredients != null ? ingredients.size() : 0) + " ingrédients");

            if (ingredients == null || ingredients.isEmpty()) {
                Log.e(TAG, "setIngredients: Liste d'ingrédients null ou vide");
                _ingredients.setValue(new ArrayList<>());
                return;
            }

            // Créer une nouvelle liste pour éviter les problèmes de référence
            List<Ingredient> newList = new ArrayList<>(ingredients);

            // Log détaillé pour chaque ingrédient
            for (int i = 0; i < newList.size(); i++) {
                Ingredient ing = newList.get(i);
                Log.d(TAG, "Ingrédient " + (i+1) + ": " + ing.getName() +
                        " (" + ing.getQuantity() + " " + ing.getUnit() + ")");
            }

            // Mise à jour du LiveData
            _ingredients.setValue(newList);

            // Vérification que la liste a bien été mise à jour
            List<Ingredient> currentList = _ingredients.getValue();
            if (currentList != null) {
                Log.d(TAG, "Après setIngredients, _ingredients contient " + currentList.size() + " ingrédients");
            } else {
                Log.e(TAG, "Après setIngredients, _ingredients est null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception dans setIngredients", e);
            e.printStackTrace();
        }
    }

    /**
     * Obtient la LiveData contenant la liste des ingrédients
     */
    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    /**
     * Ajoute un ingrédient à la liste
     */
    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) return;

        List<Ingredient> currentList = _ingredients.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        // Vérifier si l'ingrédient existe déjà
        boolean exists = false;
        for (Ingredient existing : currentList) {
            if (existing.getName().equals(ingredient.getName())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            // Créer une nouvelle liste pour éviter les problèmes de référence
            List<Ingredient> newList = new ArrayList<>(currentList);
            newList.add(ingredient);
            _ingredients.setValue(newList);

            Log.d(TAG, "Ingrédient ajouté: " + ingredient.getName() + ", total: " + newList.size());
        }
    }

    /**
     * Supprime un ingrédient de la liste
     */
    public void removeIngredient(Ingredient ingredient) {
        List<Ingredient> currentList = _ingredients.getValue();
        if (currentList != null && ingredient != null) {
            List<Ingredient> newList = new ArrayList<>(currentList);
            newList.remove(ingredient);
            _ingredients.setValue(newList);

            Log.d(TAG, "Ingrédient supprimé: " + ingredient.getName() + ", restants: " + newList.size());
        }
    }

    /**
     * Calcule le total des calories des ingrédients
     */
    public int getTotalCalories() {
        List<Ingredient> ingredients = _ingredients.getValue();
        if (ingredients == null) return 0;

        int total = 0;
        for (Ingredient ingredient : ingredients) {
            total += ingredient.getCalories();
        }
        return total;
    }

    /**
     * Récupère la recette actuelle
     * @return La recette actuelle ou null si aucune n'est définie
     */
    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    /**
     * Définit la recette actuelle
     * @param recipe La recette à définir
     */
    public void setCurrentRecipe(Recipe recipe) {
        this.currentRecipe = recipe;

        // Mise à jour automatique des informations nutritionnelles
        if (recipe != null) {
            Log.d(TAG, "setCurrentRecipe - Nombre de portions reçu: " + recipe.getNbServings());
            setNutritionInfo(recipe.getCalories(), recipe.getProtein(), recipe.getCarbs(), recipe.getFat());
            setMenuName(recipe.getName());

            // Mise à jour du nombre de portions sans condition
            setParticipantCount(recipe.getNbServings());
            Log.d(TAG, "setCurrentRecipe - Nombre de participants mis à jour: " + getParticipantCount());

            // Mise à jour des étapes si disponibles
            if (recipe.getSteps() != null) {
                setRawSteps(recipe.getSteps());

                List<String> stepDescriptions = new ArrayList<>();
                for (Step step : recipe.getSteps()) {
                    stepDescriptions.add(step.getDescription());
                }
                setSteps(stepDescriptions);
            }
        }
    }
}