package com.gfaim.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.models.ForgotPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPwdActivity extends AppCompatActivity {

    private EditText emailInput;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpwd);

        emailInput = findViewById(R.id.email);
        authService = ApiClient.getClient(this).create(AuthService.class);

        setupGoBack();
        setupContinue();
    }

    private void setupGoBack() {
        TextView loginBtn = findViewById(R.id.goBackLogin);
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupContinue() {
        Button sendMailBtn = findViewById(R.id.SendMail);
        sendMailBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (!email.isEmpty()) {
                sendForgotPasswordRequest(email);
            }
        });
    }

    private void sendForgotPasswordRequest(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        Call<Void> call = authService.forgotPassword(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPwdActivity.this, R.string.codeSent, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), CheckMailActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPwdActivity.this, R.string.errorSending, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPwdActivity.this, R.string.errorConnection + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
