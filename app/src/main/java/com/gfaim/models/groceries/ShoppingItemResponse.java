package com.gfaim.models.groceries;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingItemResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("bought")
    private boolean bought;

    @SerializedName("family")
    private Family family;

    @SerializedName("ingredientCatalog")
    private IngredientCatalog ingredientCatalog;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Family {
        @SerializedName("id")
        private Long id;

        @SerializedName("name")
        private String name;

        @SerializedName("code")
        private String code;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IngredientCatalog {
        @SerializedName("id")
        private Long id;

        @SerializedName("name")
        private String name;

        @SerializedName("nameEn")
        private String nameEn;

        @SerializedName("nameFr")
        private String nameFr;
    }
}