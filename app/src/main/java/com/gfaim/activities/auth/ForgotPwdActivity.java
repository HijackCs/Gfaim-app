package com.gfaim.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gfaim.R;

import java.util.logging.Logger;

public class ForgotPwdActivity extends AppCompatActivity {

    private final Logger log = Logger.getLogger(ForgotPwdActivity.class.getName()) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);

            EdgeToEdge.enable(this);
            setContentView(R.layout.forgotpwd);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }catch (Exception e){
            log.warning("[ForgotPwdActivity][onCreate] Problem on MainActivity launch");
        }

        setupGoBack();
        setupContinue();
    }


    protected void setupGoBack(){

        log.info("[ForgotPwdActivity][setupGoBack] setup go back ");
        TextView loginBtn = findViewById(R.id.goBackLogin);
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    protected void setupContinue(){

        log.info("[ForgotPwdActivity][setupGoBack] setup go back ");
        Button sendMailBtn = findViewById(R.id.SendMail);
        sendMailBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CheckMailActivity.class);
            startActivity(intent);
        });
    }

}
