package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String code;
    @SerializedName("new_password")
    private String newPassword;


    public ResetPasswordRequest(String code, String newPassword) {
        this.code = code;
        this.newPassword = newPassword;
    }
}
