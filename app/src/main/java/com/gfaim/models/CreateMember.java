package com.gfaim.models;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateMember {
    private Long id;

    @SerializedName("has_account")
    private boolean hasAccount;
    @SerializedName("family_id")
    private Long familyId;
    @SerializedName("user_email")
    private String userEmail;

    private String role;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

}
