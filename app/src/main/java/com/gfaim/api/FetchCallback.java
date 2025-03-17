package com.gfaim.api;

import java.util.HashMap;

public interface FetchCallback {
    void onSuccess(HashMap<Integer, String> result);
    void onFailure();
}

