package com.gfaim.activities.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedStepsViewModel extends ViewModel {
    private String menuName = "";
    private int participantCount = 1;
    private final MutableLiveData<List<String>> ingredients = new MutableLiveData<>(new ArrayList<>());
    private final List<String> steps = new ArrayList<>();

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
    public LiveData<List<String>> getIngredients() {
        return ingredients;
    }

    public void addIngredient(String ingredient) {
        List<String> currentIngredients = new ArrayList<>(ingredients.getValue()); // Copie de la liste actuelle
        currentIngredients.add(ingredient);
        ingredients.setValue(currentIngredients); // Mise à jour du LiveData avec une nouvelle liste
    }


    // Gestion des étapes
    public List<String> getSteps() {
        return steps;
    }

    public void updateStep(int index, String stepText) {
        while (steps.size() <= index) {
            steps.add("");
        }
        steps.set(index, stepText);
    }
}