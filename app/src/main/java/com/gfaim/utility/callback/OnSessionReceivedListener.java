package com.gfaim.utility.callback;

import com.gfaim.models.member.MemberSessionBody;

public interface OnSessionReceivedListener {
    void onSuccess(MemberSessionBody session);
    void onFailure(Throwable error);
}

