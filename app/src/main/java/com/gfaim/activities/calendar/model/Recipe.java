package com.gfaim.activities.calendar.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("calories")
    private Object calories;

    @SerializedName("protein")
    private Object protein;

    @SerializedName("carbs")
    private Object carbs;

    @SerializedName("fat")
    private Object fat;

    @SerializedName("steps")
    private List<Step> steps;

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

    // Constructeur par défaut nécessaire pour Gson
    public Recipe() {
    }

    // Constructeur pour la compatibilité avec le code existant
    public Recipe(String name, List<String> ingredients, List<String> stepsDesc, int servings) {
        this.name = name;
        this.nbServings = servings;
        this.readyInMinutes = 30;
    }

    // Getters et Setters avec vérifications de nullité
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Convertit un objet en entier en gérant les différents types possibles
     */
    private int convertToInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                try {
                    return (int) Double.parseDouble((String) value);
                } catch (NumberFormatException e2) {
                    return 0;
                }
            }
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    public int getCalories() {
        return convertToInt(calories);
    }

    public void setCalories(Object calories) {
        this.calories = calories;
    }

    public int getProtein() {
        return convertToInt(protein);
    }

    public void setProtein(Object protein) {
        this.protein = protein;
    }

    public int getCarbs() {
        return convertToInt(carbs);
    }

    public void setCarbs(Object carbs) {
        this.carbs = carbs;
    }

    public int getFat() {
        return convertToInt(fat);
    }

    public void setFat(Object fat) {
        this.fat = fat;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getImageUri() {
        return imageUri != null ? imageUri : "";
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageType() {
        return imageType != null ? imageType : "";
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public int getPreparationMinutes() {
        return preparationMinutes != null ? preparationMinutes : 0;
    }

    public void setPreparationMinutes(Integer preparationMinutes) {
        this.preparationMinutes = preparationMinutes;
    }

    public int getCookingMinutes() {
        return cookingMinutes != null ? cookingMinutes : 0;
    }

    public void setCookingMinutes(Integer cookingMinutes) {
        this.cookingMinutes = cookingMinutes;
    }

    public int getReadyInMinutes() {
        return readyInMinutes != null ? readyInMinutes : 0;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public int getNbServings() {
        return nbServings != null ? nbServings : 0;
    }

    public void setNbServings(Integer nbServings) {
        this.nbServings = nbServings;
    }

    public double getPricePerServing() {
        return pricePerServing != null ? pricePerServing : 0.0;
    }

    public void setPricePerServing(Double pricePerServing) {
        this.pricePerServing = pricePerServing;
    }

    // Pour conserver la compatibilité avec le code existant
    public int getServings() {
        return getNbServings();
    }

    public void setServings(int servings) {
        this.nbServings = servings;
    }

    public int getTime() {
        return getReadyInMinutes();
    }

    public void setTime(int time) {
        this.readyInMinutes = time;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(id);
        sb.append(",\"name\":\"").append(getName()).append("\"");
        sb.append(",\"calories\":").append(getCalories());
        sb.append(",\"protein\":").append(getProtein());
        sb.append(",\"carbs\":").append(getCarbs());
        sb.append(",\"fat\":").append(getFat());
        sb.append(",\"steps\":[");

        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                Step step = steps.get(i);
                sb.append("{");
                sb.append("\"id\":").append(step.getId());
                sb.append(",\"description\":\"").append(step.getDescription()).append("\"");
                sb.append(",\"ingredients\":[");

                if (step.getIngredients() != null) {
                    for (int j = 0; j < step.getIngredients().size(); j++) {
                        if (j > 0) {
                            sb.append(",");
                        }
                        StepIngredient si = step.getIngredients().get(j);
                        sb.append("{");
                        sb.append("\"id\":").append(si.getId());
                        sb.append(",\"quantity\":").append(si.getQuantity());
                        sb.append(",\"unit\":{");
                        if (si.getUnit() != null) {
                            sb.append("\"id\":").append(si.getUnit().getId());
                            sb.append(",\"name_en\":\"").append(si.getUnit().getNameEn()).append("\"");
                            sb.append(",\"name_fr\":\"").append(si.getUnit().getNameFr()).append("\"");
                        }
                        sb.append("}");
                        sb.append(",\"ingredient_catalog\":{");
                        if (si.getIngredientCatalog() != null) {
                            sb.append("\"id\":").append(si.getIngredientCatalog().getId());
                            sb.append(",\"name\":\"").append(si.getIngredientCatalog().getName()).append("\"");
                            sb.append(",\"name_en\":\"").append(si.getIngredientCatalog().getNameEn()).append("\"");
                            sb.append(",\"name_fr\":\"").append(si.getIngredientCatalog().getNameFr()).append("\"");
                        }
                        sb.append("}");
                        sb.append("}");
                    }
                }
                sb.append("]");
                sb.append(",\"step_number\":").append(step.getStepNumber());
                sb.append("}");
            }
        }
        sb.append("]");
        sb.append(",\"image_uri\":\"").append(imageUri != null ? imageUri : "").append("\"");
        sb.append(",\"image_type\":\"").append(imageType != null ? imageType : "").append("\"");
        sb.append(",\"preparation_minutes\":").append(preparationMinutes != null ? preparationMinutes : 0);
        sb.append(",\"cooking_minutes\":").append(cookingMinutes != null ? cookingMinutes : 0);
        sb.append(",\"ready_in_minutes\":").append(readyInMinutes != null ? readyInMinutes : 0);
        sb.append(",\"nb_servings\":").append(nbServings != null ? nbServings : 0);
        sb.append(",\"price_per_serving\":").append(pricePerServing != null ? pricePerServing : 0.0);
        sb.append("}");
        return sb.toString();
    }
}