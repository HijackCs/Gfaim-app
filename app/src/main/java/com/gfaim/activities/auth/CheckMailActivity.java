package com.gfaim.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.models.auth.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckMailActivity extends AppCompatActivity {

    private EditText codeInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button resetPasswordBtn;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkmail);

        codeInput = findViewById(R.id.codeInput);
        newPasswordInput = findViewById(R.id.newPassword);
        confirmPasswordInput = findViewById(R.id.confirmPassword);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);

        authService = ApiClient.getClient(this).create(AuthService.class);

        setupResetPassword();
    }

    private void setupResetPassword() {
        resetPasswordBtn.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas.", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(code, newPassword);
            }
        });
    }

    private void resetPassword(String code, String newPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest(code, newPassword);
        Call<Void> call = authService.resetPassword(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CheckMailActivity.this, "Mot de passe réinitialisé avec succès.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckMailActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(CheckMailActivity.this, "Code invalide ou erreur de réinitialisation.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CheckMailActivity.this, "Erreur de connexion : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
