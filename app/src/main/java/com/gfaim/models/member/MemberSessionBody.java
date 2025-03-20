package com.gfaim.models.member;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberSessionBody {

    private Long id;

    @SerializedName("has_account")
    private boolean hasAccount;

    @SerializedName("firstName")

    private String firstName;

    @SerializedName("last_name")

    private String lastName;

    private String role;
    @SerializedName("family_id")

    private String familyId;

    @SerializedName("user_id")

    private String userId;

}
