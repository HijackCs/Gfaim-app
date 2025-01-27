package com.gfaim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.logging.Logger;

public class registerActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(registerActivity.class.getName()) ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        register();
    }

    protected void register(){

        log.info("[MainActivity][register] register ");

        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            log.info("[MainActivity][register] click ");

            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(intent);

        });
    }

}
