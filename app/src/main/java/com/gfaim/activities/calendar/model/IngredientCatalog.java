package com.gfaim.activities.calendar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Représente un ingrédient du catalogue
 */
public class IngredientCatalog {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("name_en")
    private String nameEn;

    @SerializedName("name_fr")
    private String nameFr;

    public IngredientCatalog() {
        // Constructeur par défaut nécessaire pour Gson
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    @Override
    public String toString() {
        return "IngredientCatalog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}