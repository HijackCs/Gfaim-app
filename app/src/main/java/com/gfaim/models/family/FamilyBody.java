package com.gfaim.models.family;


import com.gfaim.models.member.MemberSessionBody;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FamilyBody {

    private Long id;

    private String name;

    private String code;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    private List<MemberSessionBody> members = new ArrayList<>();


    @Override
    public String toString() {
        return "FamilyBody{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }


    public List<MemberSessionBody> getMembers() {
        return members;
    }
}