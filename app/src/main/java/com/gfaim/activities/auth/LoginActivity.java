package com.gfaim.activities.auth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.home.HomeActivity;
import com.gfaim.activities.auth.onboarding.OnBoardingActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.AuthResponse;
import com.gfaim.models.LoginRequest;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.auth.AuthManager;
import com.gfaim.utility.auth.JwtDecoder;
import com.gfaim.utility.callback.OnMemberReceivedListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
            setContentView(R.layout.loginv2);
            utileProfile = new UtileProfile(this);

        } catch (Exception e) {
            log.warning("[LoginActivity][onCreate] Problem on MainActivity launch");
        }

        //ImageButton googleBtn = findViewById(R.id.googleButton);
        //ImageButton facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
       // authManager.setupGoogleLogin(googleBtn, this);
       // authManager.setupFacebookLogin(facebookBtn, this);

        setupClassicLogin();
        setupRegister();
        setupForgotPwd();
        setupHidePwd();
    }

    private String getUserEmail() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("upn")) {
                System.out.println(jsonObject);
                return jsonObject.get("upn").getAsString();
            }
        }
        return "";
    }

    public void doesheHaveAFamily(Response<AuthResponse> response){

        tokenManager.saveTokens(response.body().getAccessToken(), response.body().getRefreshToken());

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {

            }

            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                    activity.finish();
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);

            }
            @Override
            public void onFailure(Throwable error) {
                activity.finish();
                Intent intent = new Intent(activity, OnBoardingActivity.class);
                activity.startActivity(intent);
              log.info("Erreur lors de la récupération de la session : " + error.getMessage());
            }

            @Override
            public void onSuccess(CreateMember body) {

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
        TextView loginError = findViewById(R.id.loginError);
        emailInput.setBackgroundResource(R.drawable.rounded_border);
        passwordInput.setBackgroundResource(R.drawable.rounded_border);
        loginError.setVisibility(GONE);
        loginError.setText("");


        Call<AuthResponse> call = authService.login(new LoginRequest(email, password));
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doesheHaveAFamily(response);
                } else {
                    if (email.isEmpty()) {
                        emailInput.setError("Veuillez entrer votre email.");
                        emailInput.setBackgroundResource(R.drawable.rounded_border_error);
                    }
                    if (password.isEmpty()) {
                        passwordInput.setError("Veuillez entrer votre mot de passe.");
                        passwordInput.setBackgroundResource(R.drawable.rounded_border_error);

                    }
                    loginError.setText("Email ou mot de passe incorrect.");
                    emailInput.setBackgroundResource(R.drawable.rounded_border_error);
                    passwordInput.setBackgroundResource(R.drawable.rounded_border_error);
                    loginError.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginError.setText("Erreur: " + t.getMessage());
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