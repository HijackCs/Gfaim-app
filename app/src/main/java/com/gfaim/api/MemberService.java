package com.gfaim.api;

import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.CreateSelfMemberBody;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.models.member.UpdateMember;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MemberService {
    @POST("/members")
    Call<CreateMember> createMember(@Header("Authorization") String token, @Body CreateSelfMemberBody request);

    @POST("/members")
    Call<CreateMemberNoAccount> createMember(@Header("Authorization") String token, @Body CreateMemberNoAccount request);
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

    @PATCH("members/{memberId}")
    Call<UpdateMember> updateMember(
            @Path("memberId") Long memberId,
            @Header("Authorization") String token,
            @Body UpdateMember updateMember
            );

    @GET("/members/session")
    Call<MemberSessionBody> getMemberSession();

    @DELETE("members/{memberId}")
    Call<MemberSessionBody> deleteMember(@Path("memberId") Long memberId);

}


