package com.gfaim.activities.auth;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.home.HomeActivity;
import com.gfaim.utility.auth.AuthManager;

import java.util.logging.Logger;

public class RegisterActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(RegisterActivity.class.getName());

    private EditText surname;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button signUpBtn;
    private CheckBox termsCheckBox;
    private TextView loginBtn;
    private TextView termsText;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initializeUI();
        setupTextWatchers();
        setupAuthButtons();
        setupLoginBtn();
        setupRegisterBtn();
        setupTermsTextLink();

    }

    private void initializeUI() {
        surname = findViewById(R.id.surname);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signUpBtn = findViewById(R.id.SignUpBtn);
        loginBtn = findViewById(R.id.loginBtn);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        termsText = findViewById(R.id.termsText);
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkFields();
        });

        signUpBtn.setEnabled(false);
        signUpBtn.setAlpha(0.5f);
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        surname.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    private void setupAuthButtons() {
        log.info("[RegisterActivity][setupAuthButtons] setup auth Btn ");

        LinearLayout googleBtn = findViewById(R.id.googleButton);
        LinearLayout facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
        authManager.setupGoogleLogin(googleBtn, this);
        authManager.setupFacebookLogin(facebookBtn, this);
    }

    protected void setupRegisterBtn() {
        log.info("[RegisterActivity][setupRegisterBtn] setup Register Btn ");

        signUpBtn.setOnClickListener(v -> {
            log.info("[RegisterActivity][register] register ");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });
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

        if (!isValidName(surname.getText().toString())) {
            surname.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            surname.setBackgroundResource(R.drawable.rounded_border);
        }

        if (!isValidName(name.getText().toString())) {
            name.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            name.setBackgroundResource(R.drawable.rounded_border);
        }

        if (!isValidEmail(email.getText().toString())) {
            email.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            email.setBackgroundResource(R.drawable.rounded_border);
        }

        if (password.getText().toString().trim().isEmpty()) {
            password.setBackgroundResource(R.drawable.rounded_border_error);
        } else {
            password.setBackgroundResource(R.drawable.rounded_border);
        }


        if (!termsAccepted) {
            termsCheckBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.red));
        } else {
            termsCheckBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.black));
        }

    }

    private boolean isFormValid() {
        return isValidName(surname.getText().toString())
                && isValidName(name.getText().toString())
                && isValidEmail(email.getText().toString())
                && !password.getText().toString().trim().isEmpty();
    }

    private boolean isValidName(String name) {
        return name != null && name.matches("[A-Za-zÀ-ÿ]+");
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
