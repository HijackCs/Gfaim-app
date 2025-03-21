package com.gfaim.utility.callback;

import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.family.LeaveFamilyBody;

public interface OnFamilyReceivedListener {

    void onSuccess();
    void onSuccess(LeaveFamilyBody family);

    void onSuccess(CreateFamilyBody family);
    void onSuccess(FamilyBody family);
    void onFailure(Throwable error);
}
