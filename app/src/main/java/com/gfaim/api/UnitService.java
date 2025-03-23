package com.gfaim.api;

import com.gfaim.activities.calendar.model.Unit;

import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class UnitService {
    /*@GET("/units")
    Call<List<Unit>> getUnits(@Header("Authorization") String token);

    @GET("/units/{id}")
    Call<List<Unit>> getUnitById(@Path("id") Long id,
                              @Header("Authorization") String token);*/
}
