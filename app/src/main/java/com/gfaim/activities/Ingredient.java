package com.gfaim.activities;

public class Ingredient implements java.io.Serializable {
    private String name;
    private int calories;
    private int grams;

    // Constructeur
    public Ingredient(String name, int calories, int grams) {
        this.name = name;
        this.calories = calories;
        this.grams = grams;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getGrams() {
        return grams;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setGrams(int grams) {
        this.grams = grams;
    }

    // Méthode pour calculer les calories totales selon les grammes
    public int getTotalCalories() {
        return (calories * grams) / 100;
    }

    @Override
    public String toString() {
        return name + " - " + grams + "g - " + getTotalCalories() + " kcal";
    }
}
