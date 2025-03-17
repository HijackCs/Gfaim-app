package com.gfaim.api;

import com.gfaim.models.CreateFamily;
import com.gfaim.models.CreateFamilyBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FamilyService {
    @POST("/families")
    Call<CreateFamily> createFamily(@Header("Authorization") String token,@Body CreateFamilyBody request);
}
