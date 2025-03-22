package com.gfaim.api;

import com.gfaim.activities.calendar.model.Unit;

import java.util.List;

public class UnitService {
    @GET("/units")
    Call<List<Unit>> getUnits(@Header("Authorization") String token);

    @GET("/units/{id}")
    Call<List<Unit>> getUnitById(@Path("id") Long id,
                              @Header("Authorization") String token);
}
