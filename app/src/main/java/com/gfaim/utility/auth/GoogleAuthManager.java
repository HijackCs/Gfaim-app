package com.gfaim.utility.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.gfaim.activities.home.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.logging.Logger;

public class GoogleAuthManager {
    private GoogleSignInClient gsc;
    private final Logger log = Logger.getLogger(GoogleAuthManager.class.getName());
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1000;

    public GoogleAuthManager(Activity activity) {
        try{
            log.info("[GoogleAuthManager] Google init");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            gsc = GoogleSignIn.getClient(activity, gso);
        }catch(Exception e){
            log.info("[GoogleAuthManager] Google init failed :" + e);
        }

    }

    public void startGoogleLogin(Activity activity) {

        try{
            log.info("[GoogleAuthManager][startGoogleLogin] Google start sign in");
            Intent signInIntent = gsc.getSignInIntent();
            activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
        }catch(Exception e){
            log.info("[GoogleAuthManager][startGoogleLogin] Google start sign in failed : " + e);
        }
    }

    //Faire la verif inscription ici
    public void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                log.info("Google Login Successful: " + account.getEmail());
                activity.finish();
                Intent intent = new Intent(activity, HomeActivity.class);
                activity.startActivity(intent);
            } catch (ApiException e) {
                log.warning("Google Login Failed: " + e.getMessage());
                Toast.makeText(activity, "Google login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
