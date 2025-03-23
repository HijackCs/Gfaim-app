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
public class RecipeResponseBody {

    public Long getId() {
        return id;
    }
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("image_uri")
    private String imageUri;

    @SerializedName("image_type")
    private String imageType;

    @SerializedName("preparation_minutes")
    private Integer preparationMinutes;

    @SerializedName("cooking_minutes")
    private Integer cookingMinutes;

    @SerializedName("ready_in_minutes")
    private Integer readyInMinutes;

    @SerializedName("nb_servings")
    private Integer nbServings;

    @SerializedName("price_per_serving")
    private Double pricePerServing;

    @SerializedName("calories")
    private Integer calories;

    @SerializedName("protein")
    private Integer protein;

    @SerializedName("carbs")
    private Integer carbs;

    @SerializedName("fat")
    private Integer fat;

    @SerializedName("steps")
    private List<RecipeStepResponse> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Integer getPreparationMinutes() {
        return preparationMinutes;
    }

    public void setPreparationMinutes(Integer preparationMinutes) {
        this.preparationMinutes = preparationMinutes;
    }

    public Integer getCookingMinutes() {
        return cookingMinutes;
    }

    public void setCookingMinutes(Integer cookingMinutes) {
        this.cookingMinutes = cookingMinutes;
    }

    public Integer getNbServings() {
        return nbServings;
    }

    public void setNbServings(Integer nbServings) {
        this.nbServings = nbServings;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public Double getPricePerServing() {
        return pricePerServing;
    }

    public void setPricePerServing(Double pricePerServing) {
        this.pricePerServing = pricePerServing;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getCarbs() {
        return carbs;
    }

    public void setCarbs(Integer carbs) {
        this.carbs = carbs;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }

    public List<RecipeStepResponse> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStepResponse> steps) {
        this.steps = steps;
    }
}