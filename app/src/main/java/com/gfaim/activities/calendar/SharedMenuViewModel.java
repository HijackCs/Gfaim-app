package com.gfaim.activities.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/*public class SharedMenuViewModel extends ViewModel {
    private String menuName = "";
    private int participantCount = 0;
    private final MutableLiveData<List<String>> ingredients = new MutableLiveData<>(new ArrayList<>()); // Stockage centralisé des ingrédients
    private final List<String> steps = new ArrayList<>(); // Conserver les étapes ici aussi

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    // Ajout d'un ingrédient avec mise à jour du LiveData
    public void addIngredient(String ingredient) {
        List<String> currentIngredients = ingredients.getValue();
        if (currentIngredients != null) {
            currentIngredients.add(ingredient);
            ingredients.setValue(currentIngredients);
        }
    }

    // Récupération des ingrédients via LiveData pour observer les changements
    public LiveData<List<String>> getIngredients() {
        return ingredients;
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
}*/
