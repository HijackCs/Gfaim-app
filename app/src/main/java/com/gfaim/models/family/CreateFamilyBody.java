package com.gfaim.models.family;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateFamilyBody {
    @SerializedName("family_name")
    private String familyName;

    public String getFamilyName() {
        return familyName;
    }

    @Override
    public String toString() {
        return "CreateFamilyBody{" +
                "familyName='" + familyName + '\'' +
                '}';
    }
}
