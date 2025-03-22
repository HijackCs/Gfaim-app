package com.gfaim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.gfaim.R;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.activities.settings.family.FamilyActivity;
import com.gfaim.activities.settings.family.NewMemberActivity;
import com.gfaim.activities.groceries.GroceryActivity;
import com.gfaim.activities.home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.logging.Logger;

public class LoadingActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(LoadingActivity.class.getName()) ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        log.info("[LoadingActivity][onCreate] Application starting");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        Runnable runnable = () -> {
            Class<?> targetActivity = checkSession() ? HomeActivity.class : LoginActivity.class;
            Intent intent = new Intent(getApplicationContext(), targetActivity);
            startActivity(intent);
            finish();
        };

        new Handler(Looper.getMainLooper()).postDelayed(runnable, 3000);
    }


    private boolean checkSession() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleAccount != null) {
            log.info("[LoadingActivity][checkSession] User logged in with Google");
            return true;
        }

        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null && !facebookAccessToken.isExpired()) {
            log.info("[LoadingActivity][checkSession] User logged in with Facebook");
            return true;
        }

        log.info("[LoadingActivity][checkSession] No active session found");
        return false;
    }
}