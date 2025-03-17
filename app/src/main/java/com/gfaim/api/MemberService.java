package com.gfaim.api;

import com.gfaim.models.CreateMember;
import com.gfaim.models.CreateSelfMemberBody;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MemberService {
    @POST("/members")
    Call<CreateMember> createMember(@Header("Authorization") String token, @Body CreateSelfMemberBody request);

    @POST("members/{memberId}/allergies")
    Call<Void> postAllergies(
            @Path("memberId") Long memberId,
            @Header("Authorization") String token,
            @Body List<Map<String, Integer>> allergies
    );

    @POST("members/{memberId}/diets")
    Call<Void> postDiets(
            @Path("memberId") Long memberId,
            @Header("Authorization") String token,
            @Body List<Map<String, Integer>> diets
    );
}
