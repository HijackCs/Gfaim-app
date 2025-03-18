package com.gfaim.utility.callback;

import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;

public interface OnFamilyReceivedListener {

    void onSuccess(CreateFamilyBody family);
    void onSuccess(FamilyBody family);
    void onFailure(Throwable error);
}
