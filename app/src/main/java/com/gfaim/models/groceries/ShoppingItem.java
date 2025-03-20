package com.gfaim.models.groceries;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShoppingItem {
    @SerializedName("ingredient_catalog_id")
    private Long ingredientCatalogId;

    @SerializedName("ingredient_name")
    private String ingredientName;

    @SerializedName("ingredient_name_fr")
    private String ingredientNameFr;

    @SerializedName("ingredient_name_en")
    private String ingredientNameEn;

    @SerializedName("bought")
    private boolean bought;
}
