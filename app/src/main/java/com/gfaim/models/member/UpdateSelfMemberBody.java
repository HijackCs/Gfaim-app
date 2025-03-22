package com.gfaim.models.member;


import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateSelfMemberBody {

    @SerializedName("has_account")
    private boolean hasAccount;
    @SerializedName("family_code")
    private String familyCode;
    @SerializedName("user_email")
    private String userEmail;

}
