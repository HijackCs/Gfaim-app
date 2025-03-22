package com.gfaim.utility.callback;

import com.gfaim.models.UpdateUserBody;
import com.gfaim.models.user.UpdateUserPassword;

public interface OnUserReceivedListener {


    void onSuccess(UpdateUserBody session);

    void onSuccess(UpdateUserPassword session);

    void onFailure(Throwable error);


}
