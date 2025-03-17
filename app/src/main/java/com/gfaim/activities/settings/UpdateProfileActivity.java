package com.gfaim.activities.settings;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.utility.api.UtileProfile;

public class UpdateProfileActivity extends AppCompatActivity {

    private UtileProfile utileProfile;
    private EditText firstName;
    private EditText lastName;

    private EditText passwordInput;
    private TextView updateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        utileProfile = new UtileProfile(this);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        passwordInput = findViewById(R.id.password);
        updateButton = findViewById(R.id.updateBtn);

        getAllInfo();

        backBtnSetup();
        editPhotoBtnSetup();

        setupTextWatchers();
        checkIfFieldsAreFilled();
        setupUpdateButton();

    }

    public void getAllInfo(){
        String getCompleteName = utileProfile.getCompleteName();
        TextView userName = findViewById(R.id.user_name);
        userName.setText(getCompleteName);

        String getFirstName = utileProfile.getFirstName();
        firstName.setHint(getFirstName);

        String getLastName = utileProfile.getLastName();
        lastName.setHint(getLastName);

        String getEmail = utileProfile.getUserEmail();
        TextView email = findViewById(R.id.email);
        email.setText(getEmail);
    }

    public void backBtnSetup(){

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(UpdateProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    public void editPhotoBtnSetup(){
        FrameLayout editPhoto = findViewById(R.id.editPhoto);
        editPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView profileImage = findViewById(R.id.profile_image);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);
        }
    }

    public void ShowHidePass(View view) {
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

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFieldsAreFilled();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
    }

    private void checkIfFieldsAreFilled() {
        boolean isAnyFieldFilled = !firstName.getText().toString().trim().isEmpty() ||
                !lastName.getText().toString().trim().isEmpty() ||
                !passwordInput.getText().toString().trim().isEmpty();

        updateButton.setEnabled(isAnyFieldFilled);
        updateButton.setAlpha(isAnyFieldFilled ? 1.0f : 0.5f);
    }

    private void setupUpdateButton() {
        updateButton.setOnClickListener(v -> {
            String firstNameValue = firstName.getText().toString().trim().isEmpty() ? firstName.getHint().toString() : firstName.getText().toString().trim();
            String lastNameValue = lastName.getText().toString().trim().isEmpty() ? lastName.getHint().toString() : lastName.getText().toString().trim();
            String passwordValue = passwordInput.getText().toString().trim().isEmpty() ? "********" : passwordInput.getText().toString().trim(); // Pour éviter d'afficher un mot de passe vide


            // Afficher les valeurs dans un Toast
            System.out.println( "First Name: " + firstNameValue +
                    "\nLast Name: " + lastNameValue +
                    "\nPassword: " + passwordValue);
    });
    }

}
