package com.gfaim.api;

import com.gfaim.models.CreateMealBody;
import com.gfaim.models.Meal;
import com.gfaim.models.MealResponseBody;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MealService {
    @GET("/meals")
    Call<List<Meal>> getMeals(@Header("Authorization") String token);
    @GET("/families/{id}/meals")
    Call<List<Meal>> getMeals(@Path("id") Long id,
                              @Header("Authorization") String token);

    @POST("/families/{id}/meals")
    Call<CreateMealBody> createMeal(@Path("id") Long id,@Body CreateMealBody request);
/*
    @PATCH("/families/{id}/meals/{mealId}")
    Call<CreateMealBody> updateMeal(
            @Path("id") Long families_id,
            @Path("mealId") Long meals_id,
            @Header("Authorization") String token,
            @Body CreateMealBody updateMeal
    );

    @DELETE("/families/{id}/meals/{mealId}")
    Call<CreateMealBody> deleteMeal(
            @Path("id") Long families_id
            @Path("mealId") Long meals_id
    );*/
}
