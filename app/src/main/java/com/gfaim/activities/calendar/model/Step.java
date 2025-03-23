package com.gfaim.activities.calendar.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une étape de préparation dans une recette
 */
public class Step {
    @SerializedName("id")
    private Long id;

    @SerializedName("description")
    private String description;

    @SerializedName("step_number")
    private int stepNumber;

    @SerializedName("ingredients")
    private List<StepIngredient> ingredients = new ArrayList<>();

    public Step() {
        // Constructeur par défaut nécessaire pour Gson
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public List<StepIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<StepIngredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", stepNumber=" + stepNumber +
                ", description='" + description + '\'' +
                ", ingredients=" + (ingredients != null ? ingredients.size() : 0) +
                '}';
    }
}