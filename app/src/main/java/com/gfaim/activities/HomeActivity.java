package com.gfaim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.gfaim.R;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.activities.groceries.GroceryActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class HomeActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(HomeActivity.class.getName()) ;
    private TextView email;
    private TextView name;
    private GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            EdgeToEdge.enable(this);
            setContentView(R.layout.home);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            name = findViewById(R.id.name);
            email = findViewById(R.id.email);
            Button signOutBtn = findViewById(R.id.signOutBtn);
            signOutBtn.setOnClickListener(v -> signOut());

            handleGoogleLogin();
            handleFacebookLogin();


        } catch (Exception e) {
            log.warning("[acceuil][onCreate] Problem on MainActivity launch");
        }

    }
    public void handleGoogleLogin() {

        // Google Sign-In variables
        GoogleSignInOptions gso;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            email.setText(personEmail);
            log.info("[acceuil][handleGoogleLogin] logged in with Google");
            Toast.makeText(this, "Logged in with Google", Toast.LENGTH_SHORT).show();
        }
    }



    private void handleFacebookLogin() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(accessToken, (jsonObject, graphResponse) -> {
                if (jsonObject != null) {
                    try {
                        String fullName = jsonObject.getString("name");
                        String mail = jsonObject.getString("email");

                        name.setText(fullName);
                        email.setText(mail);
                        log.warning("[acceuil][handleFacebookLogin] logged in with Facebook");
                        Toast.makeText(HomeActivity.this, "Logged in with Facebook", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        log.warning("[acceuil][handleFacebookLogin] Error parsing Facebook user info: " + e.getMessage());
                    }
                }
            });

            // Request Facebook user details
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private void signOut() {
                goToLoginScreen();
  /*      GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleAccount != null) {
            gsc.signOut().addOnCompleteListener(task -> {
                Toast.makeText(HomeActivity.this, "Signed out from Google", Toast.LENGTH_SHORT).show();
                log.info("[acceuil][signOut] Signed out from Google ");
            });
        }

        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null) {
            com.facebook.login.LoginManager.getInstance().logOut();
            Toast.makeText(HomeActivity.this, "Signed out from Facebook", Toast.LENGTH_SHORT).show();
            log.info("[acceuil][signOut] Signed out from Google ");
            goToLoginScreen();
        }*/
    }


    private void goToLoginScreen() {
        Intent intent = new Intent(HomeActivity.this, GroceryActivity.class);
        startActivity(intent);
        finish();
    }

}
