package com.gfaim.activities.calendar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Représente un ingrédient utilisé dans une étape de recette
 */
public class StepIngredient {
    @SerializedName("id")
    private Long id;

    @SerializedName("ingredient_catalog")
    private IngredientCatalog ingredientCatalog;

    @SerializedName("quantity")
    private float quantity;

    @SerializedName("unit")
    private Unit unit;

    public StepIngredient() {
        // Constructeur par défaut nécessaire pour Gson
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IngredientCatalog getIngredientCatalog() {
        return ingredientCatalog;
    }

    public void setIngredientCatalog(IngredientCatalog ingredientCatalog) {
        this.ingredientCatalog = ingredientCatalog;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "StepIngredient{" +
                "id=" + id +
                ", ingredient=" + (ingredientCatalog != null ? ingredientCatalog.getName() : "null") +
                ", quantity=" + quantity +
                ", unit=" + (unit != null ? unit.getNameFr() : "null") +
                '}';
    }
}