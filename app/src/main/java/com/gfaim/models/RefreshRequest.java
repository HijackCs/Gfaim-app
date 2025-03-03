package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class RefreshRequest {
    @SerializedName("refresh_token")
    private String refreshToken;

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
