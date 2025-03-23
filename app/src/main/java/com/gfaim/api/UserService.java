package com.gfaim.api;

import com.gfaim.models.user.UpdateUserBody;
import com.gfaim.models.user.UpdateUserPassword;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
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


