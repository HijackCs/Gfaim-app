package com.gfaim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.logging.Logger;

public class loadingActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(loadingActivity.class.getName()) ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        log.info("[loadingActivity][onCreate] Application starting");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Class<?> targetActivity = checkSession() ? acceuilActivity.class : loginActivity.class;
                Intent intent = new Intent(getApplicationContext(), targetActivity);
                startActivity(intent);
                finish();
            }
        };

        new Handler().postDelayed(runnable, 3000);
    }


    private boolean checkSession() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleAccount != null) {
            log.info("[loadingActivity][checkSession] User logged in with Google");
            return true;
        }

        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null && !facebookAccessToken.isExpired()) {
            log.info("[loadingActivity][checkSession] User logged in with Facebook");
            return true;
        }

        log.info("[loadingActivity][checkSession] No active session found");
        return false;
    }
}