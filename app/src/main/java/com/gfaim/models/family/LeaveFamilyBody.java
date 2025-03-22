package com.gfaim.models.family;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LeaveFamilyBody {
    @SerializedName("member_id")
    private Long memberId;
}
