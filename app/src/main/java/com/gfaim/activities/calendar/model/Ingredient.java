package com.gfaim.activities.calendar.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;
    private int calories;
    private double quantity;
    private String unit;

    public Ingredient(String name, int calories) {
        this.name = name;
        this.calories = calories;
        this.quantity = 0;
        this.unit = "";
    }

    public Ingredient(String name, int calories, double quantity, String unit) {
        this.name = name;
        this.calories = calories;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        if (quantity > 0 && !unit.isEmpty()) {
            return String.format("%s (%.1f %s)", name, quantity, unit);
        } else {
            return name;
        }
    }
}
