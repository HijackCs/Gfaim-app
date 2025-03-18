package com.gfaim.activities.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.gfaim.activities.HomeActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.AuthResponse;
import com.gfaim.models.LoginRequest;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.auth.AuthManager;
import com.gfaim.utility.callback.OnSessionReceivedListener;

import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(LoginActivity.class.getName());

    private AuthManager authManager;
    private AuthService authService;
    private TokenManager tokenManager;
    private EditText emailInput;
    private EditText passwordInput;

    private final Activity activity = this;
    UtileProfile utileProfile;

    private MemberSessionBody member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.login);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            utileProfile = new UtileProfile(this);

        } catch (Exception e) {
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
        setupHidePwd();
    }


    public void doesheHaveAFamily(Response<AuthResponse> response){
        tokenManager.saveTokens(response.body().getAccessToken(), response.body().getRefreshToken());

        utileProfile.getSessionMember(new OnSessionReceivedListener() {
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                if(member.getFamilyId() == null){
                    activity.finish();
                    Intent intent = new Intent(activity, JoinFamilyActivity.class);
                    activity.startActivity(intent);
                }else{
                    activity.finish();
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                }
            }
            @Override
            public void onFailure(Throwable error) {
              log.info("Erreur lors de la récupération de la session : " + error.getMessage());
            }
        });
    }


    //Work in progress
    protected void setupClassicLogin() {
        Button loginButton;

        log.info("[LoginActivity][setupClassicLogin] classic login setup ");


        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        tokenManager = new TokenManager(this);
        authService = ApiClient.getClient(this).create(AuthService.class);

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        Call<AuthResponse> call = authService.login(new LoginRequest(email, password));
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doesheHaveAFamily(response);
                } else {
                    if (email.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Incorrect email.", Toast.LENGTH_SHORT).show();
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(LoginActivity.this, "Error during logging in.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authManager.handleActivityResult(requestCode, resultCode, data, this);
    }


    protected void setupRegister() {
        log.info("[LoginActivity][setupRegister] setup register ");
        TextView signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    protected void setupForgotPwd() {

        log.info("[LoginActivity][setupForgotPwd] setup forgot pwd ");
        TextView forgotPwd = findViewById(R.id.forgotPwd);
        forgotPwd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPwdActivity.class);
            startActivity(intent);
        });
    }

    private void setupHidePwd() {
        ImageView showPassButton = findViewById(R.id.show_pass_btn);
        showPassButton.setOnClickListener(v -> showHidePass());
    }

    private void showHidePass() {
        EditText passwordField = findViewById(R.id.password);
        ImageView showPassButton = findViewById(R.id.show_pass_btn);

        if (passwordField.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            showPassButton.setImageResource(R.drawable.ic_eye_close);
            passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            showPassButton.setImageResource(R.drawable.ic_eye_open);
            passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


}