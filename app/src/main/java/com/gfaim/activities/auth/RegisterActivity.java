package com.gfaim.activities.auth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.auth.onboarding.OnBoardingActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.auth.AuthResponse;
import com.gfaim.models.auth.SignupRequest;
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

    private final Activity activity = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerv2);

        initializeUI();
        setupTextWatchers();
        setupAuthButtons();
        setupLoginBtn();
        signUpBtn.setOnClickListener(v -> signUp());
        setupHidePwd();
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

       // LinearLayout googleBtn = findViewById(R.id.googleButton);
        //LinearLayout facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
//        authManager.setupGoogleLogin(googleBtn, this);
  //      authManager.setupFacebookLogin(facebookBtn, this);
    }

    private void signUp() {
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String firstName = firstNameInput.getText().toString();
        String password = passwordInput.getText().toString();

        TextView registerError = findViewById(R.id.registerError);
        registerError.setVisibility(GONE);
        registerError.setText("");

        tokenManager = new TokenManager(this);
        AuthService authService = ApiClient.getClient(this).create(AuthService.class);

        if (name.isEmpty() || email.isEmpty() || firstName.isEmpty() || password.isEmpty()) {
            registerError.setText("Every fields must be filled.");
            return;
        }

        Call<AuthResponse> call = authService.signup(new SignupRequest(email, password, firstName, name));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveTokens(response.body().getAccessToken(), response.body().getRefreshToken());
                    log.info("token: " + response.body().getAccessToken());

                    Intent intent = new Intent(activity, OnBoardingActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    registerError.setText("Erreur lors de l'inscription");
                    registerError.setVisibility(VISIBLE);

                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                registerError.setText("Erreur: " + t.getMessage());
                registerError.setVisibility(VISIBLE);

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
