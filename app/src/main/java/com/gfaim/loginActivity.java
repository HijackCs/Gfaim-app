package com.gfaim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.logging.Logger;

public class loginActivity extends AppCompatActivity {

    private Logger log = Logger.getLogger(loginActivity.class.getName()) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        }catch (Exception e){
         log.warning("[MainActivity][onCreate] Problem on MainActivity launch");
        }

        classiclogin();
        fbLogin();
        googleLogin();
        register();

    }

    protected void classiclogin(){

        log.info("[MainActivity][classiclogin] classiclogin ");


        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);


        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(loginActivity.this, "MDP ou EMAIL incorrect", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(loginActivity.this, "Email: " + email + "\nPassword: " + password, Toast.LENGTH_SHORT).show();
            }

        });


    }

    protected void fbLogin(){

        log.info("[MainActivity][fbLogin] fblogin ");

        ImageButton facebookBtn = findViewById(R.id.facebookButton);

        facebookBtn.setOnClickListener(v -> {
            Toast.makeText(loginActivity.this, "Fb pas encore dispo", Toast.LENGTH_SHORT).show();

        });
    }

    protected void googleLogin(){

        log.info("[MainActivity][googleLogin] googleLogin ");

        ImageButton googleBtn = findViewById(R.id.googleButton);

        googleBtn.setOnClickListener(v -> {
            Toast.makeText(loginActivity.this, "Google pas encore dispo", Toast.LENGTH_SHORT).show();

        });
    }

    protected void register(){

        log.info("[MainActivity][register] register ");

        Button signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(), registerActivity.class);
            startActivity(intent);

        });
    }
}