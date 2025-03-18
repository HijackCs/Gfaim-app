package com.gfaim.utility.callback;

import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;

public interface OnSessionReceivedListener {
    void onSuccess(CreateMemberNoAccount session);

    void onSuccess(MemberSessionBody session);

    void onFailure(Throwable error);
}

