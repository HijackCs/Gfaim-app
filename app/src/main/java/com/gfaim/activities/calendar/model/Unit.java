package com.gfaim.activities.calendar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Représente une unité de mesure pour les ingrédients
 */
public class Unit {
    @SerializedName("id")
    private Long id;

    @SerializedName("name_en")
    private String nameEn;

    @SerializedName("name_fr")
    private String nameFr;

    public Unit() {
        // Constructeur par défaut nécessaire pour Gson
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "Unit{" +
                "id=" + id +
                ", nameFr='" + nameFr + '\'' +
                '}';
    }
}
