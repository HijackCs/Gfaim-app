package com.gfaim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gfaim.utility.auth.AuthManager;
import com.gfaim.utility.auth.FacebookAuthManager;
import com.gfaim.utility.auth.GoogleAuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.logging.Logger;

public class loginActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(loginActivity.class.getName()) ;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        }catch (Exception e){
         log.warning("[loginActivity][onCreate] Problem on MainActivity launch");
        }

        ImageButton googleBtn = findViewById(R.id.googleButton);
        ImageButton facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
        authManager.setupGoogleLogin(googleBtn, this);
        authManager.setupFacebookLogin(facebookBtn, this);

        setupClassicLogin();
        setupRegister();
    }


    //Work in progress
    protected void setupClassicLogin(){

        log.info("[loginActivity][setupClassicLogin] classic login setup ");


        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);


        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(loginActivity.this, "MDP ou EMAIL incorrect", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(loginActivity.this, "Email: " + email + "\nPassword: " + password, Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authManager.handleActivityResult(requestCode, resultCode, data, this);
    }


    protected void setupRegister(){

        log.info("[loginActivity][setupRegister] setup register ");
        TextView signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), registerActivity.class);
            startActivity(intent);
        });
    }
}