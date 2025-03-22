package com.gfaim.api;

import com.gfaim.activities.calendar.model.Recipe;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class RecipeService {
    @GET("/recipes/{recipe_id}")
    Call<List<Recipe>> getRecipes(@Path("recipe_id") Long recipe_id,
                                @Header("Authorization") String token);

    @POST("/recipes")
    Call<CreateARecipes> createMeal(@Header("Authorization") String token,@Body CreateRecipeBody request);

    @GET("/families/{id}/recipes/suggestion")
    Call<List<Recipe>> getRecipes(@Path("id") Long id,
                                  @Header("Authorization") String token);
}
