package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("description")
    private String description;

    @SerializedName("step_number")
    private Integer stepNumber;

    @SerializedName("ingredients")
    private List<RecipeStepIngrResponse> ingredients;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public List<RecipeStepIngrResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeStepIngrResponse> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "RecipeStepResponse{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", stepNumber=" + stepNumber +
                ", ingredients=" + ingredients +
                '}';
    }
}