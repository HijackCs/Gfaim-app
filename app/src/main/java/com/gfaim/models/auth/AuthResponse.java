package com.gfaim.models.auth;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;
}

