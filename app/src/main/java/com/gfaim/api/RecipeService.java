package com.gfaim.api;

import com.gfaim.activities.calendar.model.Recipe;
import com.gfaim.models.CreateRecipeBody;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface RecipeService {
    @GET("/recipes/{recipe_id}")
    Call<CreateRecipeBody> getRecipe(@Path("recipe_id") Long recipe_id);

    @POST("/recipes")
    Call<CreateRecipeBody> createRecipe(@Body CreateRecipeBody request);

    @GET("/families/{id}/recipes/suggestion")
    Call<List<Recipe>> getRecipeSuggestions(@Path("id") Long id);
}