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
public class MealResponseBody {
    @SerializedName("id")
    private Long id;

    @SerializedName("date")
    private String date;

    @SerializedName("family_id")
    private Long familyId;

    @SerializedName("recipe")
    private CreateRecipeBody recipe;

    @SerializedName("meal_type")
    private MealTypeEnum mealType;

    public CreateRecipeBody getRecipe() {
        return recipe;
    }
}