package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeStepIngrResponse {

    private Long id;
    private Integer quantity;

    @SerializedName("ingredient_catalog")
    private RecipeStepIngredientResponse ingredientCatalog;


    public RecipeStepIngredientResponse getIngredientCatalog() {
        return ingredientCatalog;
    }

    @Override
    public String toString() {
        return "RecipeStepIngrResponse{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", ingredientCatalog=" + ingredientCatalog +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setIngredientCatalog(RecipeStepIngredientResponse ingredientCatalog) {
        this.ingredientCatalog = ingredientCatalog;
    }
}
