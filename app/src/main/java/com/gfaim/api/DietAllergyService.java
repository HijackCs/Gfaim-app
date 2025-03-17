package com.gfaim.api;

import com.gfaim.models.DietAllergy;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface DietAllergyService {
    @GET("/diets")
    Call<List<DietAllergy>> getDiets(@Header("Authorization") String token);

    @GET("/allergies")
    Call<List<DietAllergy>> getAllergies(@Header("Authorization") String token);


}
