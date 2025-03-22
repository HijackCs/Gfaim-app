package com.gfaim.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep {
    @SerializedName("id")
    private Long id;

    @SerializedName("description")
    private String description;

    @SerializedName("step_number")
    private Integer stepNumber;

    @SerializedName("ingredients")
    private List<CreateRecipeStepIngrBody> ingredients;

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

    public List<CreateRecipeStepIngrBody> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<CreateRecipeStepIngrBody> ingredients) {
        this.ingredients = ingredients;
    }
}