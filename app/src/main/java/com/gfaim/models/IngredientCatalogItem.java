package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IngredientCatalogItem {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("nameEn")
    private String nameEn;

    @SerializedName("nameFr")
    private String nameFr;

    // Méthode pour obtenir le nom dans la langue actuelle du système
    public String getLocalizedName() {
        String deviceLanguage = java.util.Locale.getDefault().getLanguage();
        if (deviceLanguage.equals("fr")) {
            return nameFr;
        } else {
            return nameEn;
        }
    }
}