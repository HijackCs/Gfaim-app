package com.gfaim.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuData implements Serializable {
    private String menuName;
    private int participantCount;
    private List<String> ingredients;
    private List<String> steps;

    public MenuData() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    public List<String> getIngredients() { return ingredients; }
    public void addIngredient(String ingredient) { this.ingredients.add(ingredient); }

    public List<String> getSteps() { return steps; }
    public void addStep(String step) { this.steps.add(step); }
}
