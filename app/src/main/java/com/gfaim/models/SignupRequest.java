package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String password;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String name;

    public SignupRequest(String email, String password, String firstName, String name) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.name = name;
    }
}
