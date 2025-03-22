package com.gfaim.activities.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gfaim.activities.calendar.model.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SharedStepsViewModel extends ViewModel {
    private String menuName = "";
    private int participantCount = 1;
    private final MutableLiveData<List<Ingredient>> _ingredients = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Ingredient>> ingredients = _ingredients;
    private final MutableLiveData<List<String>> _steps = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> steps = _steps;

    private final MutableLiveData<List<Integer>> _durations = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Integer>> durations = _durations;

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

    // Gestion des ingrédients via LiveData
    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        List<Ingredient> currentIngredients = _ingredients.getValue();
        if (currentIngredients != null) {
            currentIngredients.add(ingredient);
            _ingredients.setValue(currentIngredients); // Mise à jour du LiveData
            _ingredients.setValue(currentIngredients);
        }
    }

    public void removeIngredient(Ingredient ingredient) {
        List<Ingredient> currentIngredients = _ingredients.getValue();
        if (currentIngredients != null) {
            currentIngredients.remove(ingredient);
            _ingredients.setValue(currentIngredients);
        }
    }

    public int getTotalCalories() {
        List<Ingredient> currentIngredients = _ingredients.getValue();
        if (currentIngredients == null) return 0;
        return currentIngredients.stream()
                .mapToInt(Ingredient::getCalories)
                .sum();
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

    // Méthode pour réinitialiser toutes les données
    public void reset() {
        menuName = "";
        participantCount = 1;
        _ingredients.setValue(new ArrayList<>());
        _steps.setValue(new ArrayList<>());
        _durations.setValue(new ArrayList<>());
    }

}