package com.gfaim.activities.settings;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.settings.family.FamilyActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.UserService;
import com.gfaim.models.user.UpdateUserBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.models.user.UpdateUserPassword;
import com.gfaim.utility.callback.OnMemberReceivedListener;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnUserReceivedListener;
import java.util.Objects;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private UtileProfile utileProfile;
    private EditText firstName;
    private EditText lastName;

    private EditText email;

    private EditText passwordInput;
    private TextView updateButton;
    private TextView deleteButton;
    private String getEmail;
    private String emailValue;

    private MemberSessionBody member;

    private final Logger log = Logger.getLogger(UpdateProfileActivity.class.getName());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        utileProfile = new UtileProfile(this);

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {
                //empty
            }

            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                getAllInfo();
            }
            @Override
            public void onFailure(Throwable error) {
                log.info("Error fetching session : " + error.getMessage());
            }

            @Override
            public void onSuccess(CreateMember body) {
                //empty
            }
        });

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        updateButton = findViewById(R.id.updateBtn);
        deleteButton = findViewById(R.id.deleteBtn);


        backBtnSetup();
        editPhotoBtnSetup();

        setupTextWatchers();
        checkIfFieldsAreFilled();
        setupUpdateButton();
        setupDeleteButton();

    }

    public void getAllInfo(){

        String firstNameS =  member.getFirstName();
        String lastNameS = member.getLastName();

        TextView userName = findViewById(R.id.user_name);
        userName.setText(firstNameS + " " + lastNameS);


        firstName.setHint(firstNameS);

        lastName.setHint(lastNameS);

        getEmail = utileProfile.getUserEmail();
        email.setHint(getEmail);

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
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfFieldsAreFilled();
            }

            @Override
            public void afterTextChanged(Editable s) {
            //empty
            }
        };

        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
    }


    private void checkIfFieldsAreFilled() {
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String passwordText = passwordInput.getText().toString().trim();
        String emailText = email.getText().toString().trim();

        boolean isAnyFieldFilled = !firstNameText.isEmpty() ||
                !lastNameText.isEmpty() ||
                !passwordText.isEmpty() ||
                !emailText.isEmpty();

        boolean isEmailValid = emailText.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
        if (!emailText.isEmpty() && !isEmailValid) {
            email.setError("Adresse email invalide");
        } else {
            email.setError(null);
        }

        boolean isPasswordValid = passwordText.isEmpty() || (passwordText.length() > 8 &&
                passwordText.matches(".*[A-Z].*") && // Majuscule
                passwordText.matches(".*[0-9].*") && // Chiffre
                passwordText.matches(".*[!@#$%^&*(),.?\":{}|<>].*"));

        if (!passwordText.isEmpty() && !isPasswordValid) {
            passwordInput.setError("Mot de passe invalide : 8+ caractères, 1 chiffre, 1 majuscule, 1 caractère spécial");
        } else {
            passwordInput.setError(null);
        }

        boolean shouldEnableButton = isAnyFieldFilled && (emailText.isEmpty() || isEmailValid) && (passwordText.isEmpty() || isPasswordValid);

        updateButton.setEnabled(shouldEnableButton);
        updateButton.setAlpha(shouldEnableButton ? 1.0f : 0.5f);
    }






    private void setupUpdateButton() {
        updateButton.setOnClickListener(v -> {
            String firstNameValue = firstName.getText().toString().trim().isEmpty() ? firstName.getHint().toString() : firstName.getText().toString().trim();
            String lastNameValue = lastName.getText().toString().trim().isEmpty() ? lastName.getHint().toString() : lastName.getText().toString().trim();
            String passwordValue = passwordInput.getText().toString().trim();

            emailValue = email.getText().toString().trim();
            if (emailValue.isEmpty()) {
                emailValue = getEmail;
            }

            if (passwordValue.isEmpty() || passwordValue.length() <= 8 ||
                    !passwordValue.matches(".*[A-Z].*") ||
                    !passwordValue.matches(".*[0-9].*") ||
                    !passwordValue.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                updateUser( firstNameValue, lastNameValue);

            }else{
                utileProfile.updateUserPassword(new OnUserReceivedListener() {
                    @Override
                    public void onSuccess(UpdateUserBody session) {
                        //empty
                    }
                    @Override
                    public void onSuccess(UpdateUserPassword session) {
                        //empty
                    }
                    @Override
                    public void onFailure(Throwable error) {
                        updateUser( firstNameValue, lastNameValue);
                    }
                }, member.getUserId(), passwordValue);
            }
    });
    }

    public void updateUser(String firstNameValue,String lastNameValue){
        utileProfile.updateUser(new OnUserReceivedListener() {
            @Override
            public void onSuccess(UpdateUserBody session) {
                utileProfile.getSessionMember(new OnMemberReceivedListener() {
                    @Override
                    public void onSuccess(CreateMemberNoAccount session) {
                        //empty
                    }
                    @Override
                    public void onSuccess(MemberSessionBody session) {
                        firstName.setText("");
                        lastName.setText("");
                        email.setText("");
                        passwordInput.setText("");
                        member = session;
                        getAllInfo();
                    }
                    @Override
                    public void onFailure(Throwable error) {
                        utileProfile.logout();
                        log.info("Erreur lors de la récupération de la session : " + error.getMessage());
                    }
                    @Override
                    public void onSuccess(CreateMember body) {
                        //empty
                    }
                });
            }
            @Override
            public void onSuccess(UpdateUserPassword session) {
                //empty
            }
            @Override
            public void onFailure(Throwable error) {
                //empty
            }
        },member.getUserId(), emailValue, firstNameValue, lastNameValue);
    }

    private void setupDeleteButton() {
        deleteButton.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Member")
                    .setMessage("Are you sure you want to delete your account ?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        UserService userService = ApiClient.getClient(this).create(UserService.class);
                        Call<Void> call = userService.deleteUser();
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {

                                    log.info("Successfully deleted.");
                                    utileProfile.logout();
                                } else {
                                    log.info("Error during deleting: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                log.info("Network error : " + t.getMessage());
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(dialog -> alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                    ContextCompat.getColor(this, R.color.appBackground)
            )));
            alertDialog.show();
        });
    }
}
