package com.gfaim.api;

import com.gfaim.models.AuthResponse;
import com.gfaim.models.ForgotPasswordRequest;
import com.gfaim.models.LoginRequest;
import com.gfaim.models.RefreshRequest;
import com.gfaim.models.ResetPasswordRequest;
import com.gfaim.models.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/auth/refresh")
    Call<AuthResponse> refreshToken(@Body RefreshRequest refreshRequest);

    @POST("/auth/logout")
    Call<Void> logout(@Body RefreshRequest request);

    @POST("/auth/signup")
    Call<AuthResponse> signup(@Body SignupRequest request);

    @POST("/auth/forgot")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("/auth/reset")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);


}
