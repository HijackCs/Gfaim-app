package com.gfaim.utility.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gfaim.acceuilActivity;

import java.util.Arrays;
import java.util.logging.Logger;

public class FacebookAuthManager {
    private final CallbackManager callbackManager;
    private Logger log = Logger.getLogger(FacebookAuthManager.class.getName());

    public FacebookAuthManager() {
         log.info("[FacebookAuthManager] Facebook init");
         this.callbackManager = CallbackManager.Factory.create();

    }

    public void setupFacebookLogin(ImageButton facebookBtn, Activity activity) {


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                log.info("[setupFacebookLogin][onSuccess] Facebook Login Successful");
                activity.finish();
                Intent intent = new Intent(activity, acceuilActivity.class);
                activity.startActivity(intent);
            }

            @Override
            public void onCancel() {
                log.warning("[setupFacebookLogin][onCancel] Facebook Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                log.severe("[setupFacebookLogin][onError] Facebook Login Failed: " + error.getMessage());
            }
        });

        facebookBtn.setOnClickListener(v -> {
            log.info("[FacebookAuthManager][setupFacebookLogin] Facebook login button clicked");
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
        });
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
