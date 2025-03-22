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
public class CreateRecipeStepIngrBody {

    private Integer quantity;

    @SerializedName("ingredient_catalog_id")
    private Long ingredientCatalogId;

    @SerializedName("unit_id")
    private Long unitId;

}
