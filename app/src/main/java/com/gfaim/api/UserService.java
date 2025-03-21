package com.gfaim.api;

import com.gfaim.models.UpdateUserBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.CreateSelfMemberBody;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.models.member.UpdateMember;
import com.gfaim.models.user.UpdateUserPassword;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @PUT("/users/{id}")
    Call<UpdateUserBody> updateUser(@Header("Authorization") String token,@Path("id") Long userId, @Body UpdateUserBody request);

    @PUT("/users/{id}/password")
    Call<UpdateUserPassword> updateUserPassword(@Header("Authorization") String token, @Path("id") Long userId, @Body UpdateUserPassword request);

    @DELETE("/users")
    Call<Void> deleteUser();


}


