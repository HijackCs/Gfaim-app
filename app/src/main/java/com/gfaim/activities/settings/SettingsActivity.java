package com.gfaim.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.auth.LoginActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        accountBtnSetup();
        logOutBtnSetup();

    }

    public void accountBtnSetup(){
        LinearLayout myAccount = findViewById(R.id.my_account);
        myAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });
    }

    public void logOutBtnSetup(){
        LinearLayout logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
