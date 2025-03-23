package com.gfaim.api;

import com.gfaim.models.auth.AuthResponse;
import com.gfaim.models.auth.ForgotPasswordRequest;
import com.gfaim.models.auth.LoginRequest;
import com.gfaim.models.auth.RefreshRequest;
import com.gfaim.models.auth.ResetPasswordRequest;
import com.gfaim.models.auth.SignupRequest;

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
