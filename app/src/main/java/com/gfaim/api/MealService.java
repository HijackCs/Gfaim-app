package com.gfaim.api;

import com.gfaim.models.Meal;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MealService {
    @GET("/meals")
    Call<List<Meal>> getMeals(@Header("Authorization") String token);
}

