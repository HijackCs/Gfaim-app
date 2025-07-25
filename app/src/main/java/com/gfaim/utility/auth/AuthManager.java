package com.gfaim.utility.auth;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import java.util.logging.Logger;

public class AuthManager {

    private final Logger log = Logger.getLogger(AuthManager.class.getName()) ;

    private final GoogleAuthManager googleAuthManager;
    private final FacebookAuthManager facebookAuthManager;

    public AuthManager(Activity activity) {
        googleAuthManager = new GoogleAuthManager(activity);
        facebookAuthManager = new FacebookAuthManager();
    }

    public void setupGoogleLogin(View googleBtn, Activity activity) {
        log.info("[AuthManager][setupFacebookLogin] google setup ");
        googleBtn.setOnClickListener(v -> googleAuthManager.startGoogleLogin(activity));
    }

    public void setupFacebookLogin(View facebookBtn, Activity activity) {
        log.info("[AuthManager][setupFacebookLogin] facebook setup ");
        facebookBtn.setOnClickListener(v -> facebookAuthManager.setupFacebookLogin(facebookBtn, activity));
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
        facebookAuthManager.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        googleAuthManager.handleActivityResult(requestCode, data, activity);
    }
}
