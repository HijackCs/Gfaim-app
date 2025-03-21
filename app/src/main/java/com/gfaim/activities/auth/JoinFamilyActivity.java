package com.gfaim.activities.auth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.gfaim.R;
import com.gfaim.activities.settings.SettingsActivity;
import com.gfaim.activities.settings.family.FamilyActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.FamilyService;
import com.gfaim.api.MemberService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.family.CreateFamily;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.family.JoinFamily;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.CreateSelfMemberBody;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.auth.JwtDecoder;
import com.gfaim.utility.callback.OnMemberReceivedListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinFamilyActivity extends AppCompatActivity {

    @Getter
    private String codeFamily;

    @Getter
    private Long familyId;

    @Getter
    @Setter
    private static Long memberId;

    private TokenManager tokenManager;
    private UtileProfile utileProfile;
    private MemberSessionBody member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.joinfamily);

        tokenManager = new TokenManager(this);
        utileProfile = new UtileProfile(this);

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {}
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
            }
            @Override
            public void onFailure(Throwable error) {}
            @Override
            public void onSuccess(CreateMember body) {}
        });

        Button joinFamily = findViewById(R.id.joinFamily);
        ImageView check = findViewById(R.id.check);
        check.setVisibility(View.GONE);

        joinFamily.setOnClickListener(v -> showJoinFamilyDialog());


        setupBackBtn();

    }

    private String getUserEmail() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("upn")) {
                return jsonObject.get("upn").getAsString();
            }
        }
        return "";
    }

    private void getFamillyName(View view) {
        //create member
        FamilyService familyService = ApiClient.getClient(this).create(FamilyService.class);
        Call<FamilyBody> call = familyService.getFamily(familyId, tokenManager.getAccessToken());
        TextView tvFamilyName = view.findViewById(R.id.tv_family_name);

        call.enqueue(new Callback<FamilyBody>() {
            @Override
            public void onResponse(Call<FamilyBody> call, Response<FamilyBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvFamilyName.setText( response.body().getName());
                }
            }

            @Override
            public void onFailure(Call<FamilyBody> call, Throwable t) {
            }
        });
    }

    private void showJoinFamilyDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view2 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_join_family, null);


        Button joinFamily2 = findViewById(R.id.joinFamily);
        ImageView check = findViewById(R.id.check);
        TextView tvFamilyName = view2.findViewById(R.id.tv_family_name);
        TextView tvWelcomeMessage = view2.findViewById(R.id.tv_welcome_message);
        Button joinFamily = view2.findViewById(R.id.joinFamily);
        EditText familyCode = view2.findViewById(R.id.familyCode);

        joinFamily.setEnabled(false);

        familyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isValid = !s.toString().trim().isEmpty();
                joinFamily.setEnabled(isValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty
            }
        });

        tvFamilyName.setVisibility(View.GONE);
        tvWelcomeMessage.setVisibility(View.GONE);

        joinFamily.setOnClickListener(v -> {

            codeFamily = familyCode.getText().toString();
            FamilyService familyService = ApiClient.getClient(this).create(FamilyService.class);
            Call<Void> call = familyService.joinFamily(new JoinFamily(codeFamily));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.isSuccessful()) {
                        joinFamily.setVisibility(View.GONE);
                        familyCode.setVisibility(View.GONE);
                        tvFamilyName.setVisibility(View.VISIBLE);
                        tvWelcomeMessage.setVisibility(View.VISIBLE);
                        getFamillyName(view2);
                        //Supprime les boutons parents
                        joinFamily2.setVisibility(View.GONE);
                        check.setVisibility(View.VISIBLE);
                        ((Animatable) check.getDrawable()).start();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                }
            });
        });

        bottomSheetDialog.setContentView(view2);
        bottomSheetDialog.show();
    }

    public void setupBackBtn(){
        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(JoinFamilyActivity.this, FamilyActivity.class);
            startActivity(intent);
            finish();
        });
    }

}
