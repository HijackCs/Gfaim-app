package com.gfaim.activities.auth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gfaim.R;
import com.gfaim.utility.auth.AuthManager;

import java.util.logging.Logger;

public class LoginActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(LoginActivity.class.getName()) ;

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
         log.warning("[LoginActivity][onCreate] Problem on MainActivity launch");
        }

        ImageButton googleBtn = findViewById(R.id.googleButton);
        ImageButton facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
        authManager.setupGoogleLogin(googleBtn, this);
        authManager.setupFacebookLogin(facebookBtn, this);

        setupClassicLogin();
        setupRegister();
        setupForgotPwd();

    }


    //Work in progress
    protected void setupClassicLogin(){

        log.info("[LoginActivity][setupClassicLogin] classic login setup ");


        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);


        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "MDP ou EMAIL incorrect", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "Email: " + email + "\nPassword: " + password, Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authManager.handleActivityResult(requestCode, resultCode, data, this);
    }


    protected void setupRegister(){

        log.info("[LoginActivity][setupRegister] setup register ");
        TextView signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }


    protected void setupForgotPwd(){

        log.info("[LoginActivity][setupForgotPwd] setup forgot pwd ");
        TextView forgotPwd = findViewById(R.id.forgotPwd);
        forgotPwd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPwdActivity.class);
            startActivity(intent);
        });
    }

}