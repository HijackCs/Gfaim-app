package com.gfaim.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMealBody {

    private String date;
    @SerializedName("recipe_id")
    private Long recipeId;

    @SerializedName("meal_type")
    private String mealTypeId;

    public String getDate() {
        return date;
    }

    public void setDate(String dateString) {
        this.date = dateString;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getMealType() {
        return mealTypeId;
    }

    public void setMealType(String mealTypeId) {
        this.mealTypeId = mealTypeId;
    }
}