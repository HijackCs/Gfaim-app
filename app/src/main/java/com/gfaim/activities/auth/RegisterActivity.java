package com.gfaim.activities.auth;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.auth.onboarding.OnBoardingActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.AuthResponse;
import com.gfaim.models.SignupRequest;
import com.gfaim.utility.auth.AuthManager;

import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(RegisterActivity.class.getName());

    private EditText nameInput;
    private EditText firstNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button signUpBtn;
    private CheckBox termsCheckBox;
    private TextView loginBtn;
    private TextView termsText;
    private AuthManager authManager;
    private TokenManager tokenManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initializeUI();
        setupTextWatchers();
        setupAuthButtons();
        setupLoginBtn();
        signUpBtn.setOnClickListener(v -> signUp());

        setupTermsTextLink();

    }

    private void initializeUI() {
        nameInput = findViewById(R.id.usrName);
        firstNameInput = findViewById(R.id.firstName);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        signUpBtn = findViewById(R.id.SignUpBtn);
        loginBtn = findViewById(R.id.loginBtn);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        termsText = findViewById(R.id.termsText);
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkFields());

        signUpBtn.setEnabled(false);
        signUpBtn.setAlpha(0.5f);
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty
            }
        };

        nameInput.addTextChangedListener(textWatcher);
        firstNameInput.addTextChangedListener(textWatcher);
        emailInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
    }

    private void setupAuthButtons() {
        log.info("[RegisterActivity][setupAuthButtons] setup auth Btn ");

        LinearLayout googleBtn = findViewById(R.id.googleButton);
        LinearLayout facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
        authManager.setupGoogleLogin(googleBtn, this);
        authManager.setupFacebookLogin(facebookBtn, this);
    }

    private void signUp() {
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String firstName = firstNameInput.getText().toString();
        String password = passwordInput.getText().toString();

        tokenManager = new TokenManager(this);
        AuthService authService = ApiClient.getClient(this).create(AuthService.class);

        if (name.isEmpty() || email.isEmpty() || firstName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Every fields must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<AuthResponse> call = authService.signup(new SignupRequest(email, password, firstName, name));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveTokens(response.body().getAccessToken(), response.body().getRefreshToken());
                    log.info("token: " + response.body().getAccessToken());
                    Toast.makeText(RegisterActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }});


    }



    protected void setupLoginBtn() {
        log.info("[RegisterActivity][setupLoginBtn] setup login button ");
        loginBtn.setOnClickListener(v -> {
            log.info("[RegisterActivity][setupLoginBtn] go back to login page ");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authManager.handleActivityResult(requestCode, resultCode, data, this);
    }


    private void setupTermsTextLink() {
        termsText.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cobroux.github.io/gfaim-politique-confidentialite/privacy-policy.html"));
            startActivity(browserIntent);
        });
    }

    private void checkFields() {
        boolean allFieldsValid = isFormValid();
        boolean termsAccepted = termsCheckBox.isChecked();
        signUpBtn.setEnabled(allFieldsValid && termsAccepted);
        signUpBtn.setAlpha((allFieldsValid && termsAccepted) ? 1.0f : 0.5f);

        if (!isValidName(nameInput.getText().toString())) {
            nameInput.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            nameInput.setBackgroundResource(R.drawable.rounded_border);
        }

        if (!isValidName(firstNameInput.getText().toString())) {
            firstNameInput.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            firstNameInput.setBackgroundResource(R.drawable.rounded_border);
        }

        if (!isValidEmail(emailInput.getText().toString())) {
            emailInput.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            emailInput.setBackgroundResource(R.drawable.rounded_border);
        }

        if (!isValidPassword(passwordInput.getText().toString())) {
            passwordInput.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            passwordInput.setBackgroundResource(R.drawable.rounded_border);
        }

        if (!termsAccepted) {
            termsCheckBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.red));
        } else {
            termsCheckBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.black));
        }
    }

    private boolean isFormValid() {
        return isValidName(nameInput.getText().toString())
                && isValidName(firstNameInput.getText().toString())
                && isValidEmail(emailInput.getText().toString())
                && isValidPassword(passwordInput.getText().toString());
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private boolean isValidName(String name) {
        return name != null && name.matches("[A-Za-zÀ-ÿ]+");
    }


    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
