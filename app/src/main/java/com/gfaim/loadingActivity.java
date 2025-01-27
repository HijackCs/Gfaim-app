package com.gfaim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.util.logging.Logger;

public class loadingActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(loadingActivity.class.getName()) ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        log.info("[loadingActivity][onCreate] Application starting");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        //redirige vers main

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                startActivity(intent);
                finish();
            }
        };

        new Handler().postDelayed(runnable, 3000);

    }

}
