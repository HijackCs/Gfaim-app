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
    @SerializedName("date")
    private LocalDate date;

    @SerializedName("family_id")
    private Long familyId;

    @SerializedName("recipe_id")
    private Long recipeId;

    @SerializedName("meal_type_id")
    private Long mealTypeId;
}