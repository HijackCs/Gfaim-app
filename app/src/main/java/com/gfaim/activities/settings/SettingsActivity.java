package com.gfaim.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.UserProfileActivity;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.activities.settings.family.FamilyActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.api.MealService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.RefreshRequest;
import com.gfaim.models.User;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.auth.JwtDecoder;
import com.gfaim.utility.callback.OnSessionReceivedListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {


    private UtileProfile utileProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        utileProfile = new UtileProfile(this);

        accountBtnSetup();
        logOutBtnSetup();
        familyBtnSetup();

        LinearLayout logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(v -> utileProfile.logout());


    }

    public void setupUsername(){
        utileProfile.getSessionMember(new OnSessionReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {

            }

            @Override
            public void onSuccess(MemberSessionBody session) {
                TextView userName = findViewById(R.id.user_name);
                userName.setText(session.getFirstName()+" "+session.getLastName());
            }

            @Override
            public void onFailure(Throwable error) {

            }
        });
    }

    public void accountBtnSetup(){
        LinearLayout myAccount = findViewById(R.id.my_account);
        myAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });
    }

    public void familyBtnSetup(){
        LinearLayout myAccount = findViewById(R.id.my_family);
        myAccount.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, FamilyActivity.class);
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
