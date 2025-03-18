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

    @SerializedName("first_name")

    private String firstName;

    @SerializedName("last_name")

    private String lastName;

    private String role;
    @SerializedName("family_id")

    private Long familyId;

    @SerializedName("user_id")

    private Long userId;

    @Override
    public String toString() {
        return "MemberSessionBody{" +
                "id=" + id +
                ", hasAccount=" + hasAccount +
                ", first_name='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", familyId='" + familyId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public boolean isHasAccount() {
        return hasAccount;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getRole() {
        return role;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public Long getUserId() {
        return userId;
    }
}
