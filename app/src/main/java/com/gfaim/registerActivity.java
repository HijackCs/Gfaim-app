package com.gfaim;

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

import com.gfaim.utility.auth.AuthManager;

import java.util.logging.Logger;

public class registerActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(registerActivity.class.getName());

    private EditText surname, name, email, password;
    private Button SignUpBtn;
    private CheckBox termsCheckBox;
    private TextView termsText, loginBtn;

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
        SignUpBtn = findViewById(R.id.SignUpBtn);
        loginBtn = findViewById(R.id.loginBtn);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        termsText = findViewById(R.id.termsText);
        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkFields();
        });

        SignUpBtn.setEnabled(false);
        SignUpBtn.setAlpha(0.5f);
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
        log.info("[registerActivity][setupAuthButtons] setup auth Btn ");

        LinearLayout googleBtn = findViewById(R.id.googleButton);
        LinearLayout facebookBtn = findViewById(R.id.facebookButton);

        authManager = new AuthManager(this);
        authManager.setupGoogleLogin(googleBtn, this);
        authManager.setupFacebookLogin(facebookBtn, this);
    }

    protected void setupRegisterBtn() {
        log.info("[registerActivity][setupRegisterBtn] setup Register Btn ");

        SignUpBtn.setOnClickListener(v -> {
            log.info("[registerActivity][register] register ");
            Intent intent = new Intent(getApplicationContext(), accueilActivity.class);
            startActivity(intent);
        });
    }

    protected void setupLoginBtn() {
        log.info("[registerActivity][setupLoginBtn] setup login button ");

        loginBtn.setOnClickListener(v -> {
            log.info("[registerActivity][setupLoginBtn] go back to login page ");
            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
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
        System.out.println(termsAccepted);

        SignUpBtn.setEnabled(allFieldsValid && termsAccepted);
        SignUpBtn.setAlpha((allFieldsValid && termsAccepted) ? 1.0f : 0.5f);

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
            termsCheckBox.setButtonTintList(getResources().getColorStateList(R.color.red));
        } else {
            termsCheckBox.setButtonTintList(getResources().getColorStateList(R.color.black));
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
