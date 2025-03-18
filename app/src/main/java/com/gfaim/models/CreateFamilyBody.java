package com.gfaim.models;

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
}
