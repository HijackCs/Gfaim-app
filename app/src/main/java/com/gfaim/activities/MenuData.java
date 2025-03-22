package com.gfaim.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuData implements Serializable {
    private String menuName;
    private int participantCount;
    private List<String> ingredients;
    private List<String> steps;

    private HashMap<String, Integer> ingredientCalories = new HashMap<>();

    public void addIngredient(String name, int calories) {
        ingredients.add(name);
        ingredientCalories.put(name, calories);
    }

    public int getCaloriesForIngredient(String name) {
        return ingredientCalories.getOrDefault(name, 0);
    }
    public MenuData() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    public List<String> getIngredients() { return ingredients; }

    public List<String> getSteps() { return steps; }
    public void addStep(String step) { this.steps.add(step); }
}
