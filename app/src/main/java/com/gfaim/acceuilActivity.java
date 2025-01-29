package com.gfaim;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class acceuilActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(acceuilActivity.class.getName()) ;

    // Google Sign-In variables
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private TextView name, email;
    private Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            EdgeToEdge.enable(this);
            setContentView(R.layout.acceuil);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            name = findViewById(R.id.name);
            email = findViewById(R.id.email);

            signOutBtn = findViewById(R.id.signOutBtn);
            signOutBtn.setOnClickListener(v -> signOut());

            handleGoogleLogin();
            handleFacebookLogin();


        } catch (Exception e) {
            log.warning("[acceuil][onCreate] Problem on MainActivity launch");
        }

    }


    private void handleGoogleLogin() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            email.setText(personEmail);
            log.warning("[acceuil][handleGoogleLogin] logged in with Google");
            Toast.makeText(this, "Logged in with Google", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleFacebookLogin() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                    if (jsonObject != null) {

                        System.out.println(jsonObject);
                        try {
                            String fullName = jsonObject.getString("name");
                            String mail = jsonObject.getString("email");

                            name.setText(fullName);
                            email.setText(mail);
                            log.warning("[acceuil][handleFacebookLogin] logged in with Facebook");
                            Toast.makeText(acceuilActivity.this, "Logged in with Facebook", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            log.warning("[acceuil][handleFacebookLogin] Error parsing Facebook user info: " + e.getMessage());
                        }
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
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleAccount != null) {
            gsc.signOut().addOnCompleteListener(task -> {
                Toast.makeText(acceuilActivity.this, "Signed out from Google", Toast.LENGTH_SHORT).show();
                log.info("[acceuil][signOut] Signed out from Google ");
                goToLoginScreen();
            });
        }

        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();
        if (facebookAccessToken != null) {
            com.facebook.login.LoginManager.getInstance().logOut();
            Toast.makeText(acceuilActivity.this, "Signed out from Facebook", Toast.LENGTH_SHORT).show();
            log.info("[acceuil][signOut] Signed out from Google ");
            goToLoginScreen();
        }
    }


    private void goToLoginScreen() {
        Intent intent = new Intent(acceuilActivity.this, loginActivity.class);
        startActivity(intent);
        finish();
    }

}
