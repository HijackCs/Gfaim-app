package com.gfaim.activities.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.activities.settings.family.FamilyActivity;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

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
        setupUsername();
        setupAboutBtn();
        LinearLayout logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(v -> utileProfile.logout());


    }

    public void setupAboutBtn() {
        LinearLayout aboutApp = findViewById(R.id.aboutApp);

        aboutApp.setOnClickListener(v -> {
            String url = "https://streamable.com/lf027o"; // Remplace avec ton lien
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(intent);
        });
    }


    public void setupUsername(){
        utileProfile.getSessionMember(new OnMemberReceivedListener() {
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

            @Override
            public void onSuccess(CreateMember body) {

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
