package com.gfaim.utility.callback;

import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;

public interface OnMemberReceivedListener {
    void onSuccess(CreateMemberNoAccount session);

    void onSuccess(MemberSessionBody session);

    void onFailure(Throwable error);

    void onSuccess(CreateMember body);
}

