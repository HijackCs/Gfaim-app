package com.gfaim.api;

import com.gfaim.models.family.CreateFamily;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.family.LeaveFamilyBody;
import com.gfaim.models.member.UpdateMember;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FamilyService {
    @POST("/families")
    Call<CreateFamily> createFamily(@Header("Authorization") String token,@Body CreateFamilyBody request);

    @GET("/families/{id}")
    Call<FamilyBody> getFamily(@Path("id") Long id,
                               @Header("Authorization") String token);

    @PATCH("families/{id}")
    Call<CreateFamilyBody> updateFamily(
            @Path("id") Long families_id,
            @Header("Authorization") String token,
            @Body CreateFamilyBody updateFamily
    );


    @POST("/families/{id}/leave")
    Call<LeaveFamilyBody> leaveFamily(
            @Path("id") Long families_id,
            @Header("Authorization") String token,
            @Body LeaveFamilyBody leaveFamilyBody
    );

}
