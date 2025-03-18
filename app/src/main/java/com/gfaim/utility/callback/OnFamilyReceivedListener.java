package com.gfaim.utility.callback;

import com.gfaim.models.family.FamilyBody;

public interface OnFamilyReceivedListener {
    void onSuccess(FamilyBody family);
    void onFailure(Throwable error);
}
