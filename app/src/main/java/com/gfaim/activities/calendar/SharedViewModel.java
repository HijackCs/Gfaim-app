package com.gfaim.activities.calendar;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final List<String> selectedIngredients = new ArrayList<>();

    public List<String> getSelectedIngredients() {
        return selectedIngredients;
    }

    public void addIngredient(String ingredient) {
        if (!selectedIngredients.contains(ingredient)) { // Évite les doublons
            selectedIngredients.add(ingredient);
        }
    }

    public void addIngredients(List<String> ingredients) {
        for (String ingredient : ingredients) {
            addIngredient(ingredient);
        }
    }
}
