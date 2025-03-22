package com.gfaim.activities;

public class Meal {
    private String title;
    private String text;
    private int imageResId;
    private int time;
    private int calories;

    public Meal(String title, String text, int imageResId, int time, int calories) {
        this.title = title;
        this.text = text;
        this.imageResId = imageResId;
        this.time = time;
        this.calories = calories;
    }

    // Getters et setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
}