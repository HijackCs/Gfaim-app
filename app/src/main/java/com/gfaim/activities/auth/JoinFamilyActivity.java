package com.gfaim.activities.auth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.gfaim.api.ApiClient;
import com.gfaim.api.FamilyService;
import com.gfaim.api.MemberService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.family.CreateFamily;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateSelfMemberBody;
import com.gfaim.utility.auth.JwtDecoder;
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

    private boolean displayFamilyJoin = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.joinfamily);

        tokenManager = new TokenManager(this);

        Button joinFamily = findViewById(R.id.joinFamily);
        Button createFamily = findViewById(R.id.createFamily);

        joinFamily.setOnClickListener(v -> showJoinFamilyDialog());

        createFamily.setOnClickListener(v -> showCreateFamilyDialog());

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

    private void createMember(String codeFamily) {
        //create member
        MemberService memberService = ApiClient.getClient(this).create(MemberService.class);
        String email = getUserEmail();
        Call<CreateMember> call = memberService.createMember("Bearer " + tokenManager.getAccessToken(), new CreateSelfMemberBody(true, codeFamily, email));
        call.enqueue(new Callback<CreateMember>() {
            @Override
            public void onResponse(Call<CreateMember> call, Response<CreateMember> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setMemberId(response.body().getId());
                    familyId = response.body().getFamilyId();
                } else {
                }
            }

            @Override
            public void onFailure(Call<CreateMember> call, Throwable t) {
            }
        });
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

                } else {
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


        Button createFamily = findViewById(R.id.createFamily);
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
            MemberService memberService = ApiClient.getClient(this).create(MemberService.class);
            String email = getUserEmail();
            Call<CreateMember> call = memberService.createMember("Bearer " + tokenManager.getAccessToken(), new CreateSelfMemberBody(true, codeFamily, email));
            call.enqueue(new Callback<CreateMember>() {
                @Override
                public void onResponse(Call<CreateMember> call, Response<CreateMember> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        setMemberId(response.body().getId());
                        familyId = response.body().getFamilyId();
                        displayFamilyJoin = false;
                        joinFamily.setVisibility(View.GONE);
                        familyCode.setVisibility(View.GONE);
                        tvFamilyName.setVisibility(View.VISIBLE);
                        tvWelcomeMessage.setVisibility(View.VISIBLE);
                        getFamillyName(view2);
                        //Supprime les boutons parents
                        createFamily.setVisibility(View.GONE);
                        joinFamily2.setVisibility(View.GONE);
                        check.setVisibility(View.VISIBLE);
                        ((Animatable) check.getDrawable()).start();
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<CreateMember> call, Throwable t) {
                }
            });
        });

        bottomSheetDialog.setContentView(view2);
        bottomSheetDialog.show();
    }

    private void showCreateFamilyDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view2 = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_create_family, null);

        Button createFamily = findViewById(R.id.createFamily);
        Button joinFamily = findViewById(R.id.joinFamily);
        ImageView check = findViewById(R.id.check);

        TextView newFam = view2.findViewById(R.id.new_fam);
        TextView tvFamilyCode = view2.findViewById(R.id.textView);
        Button createFamily2 = view2.findViewById(R.id.createFamily);
        EditText familyName = view2.findViewById(R.id.familyName);
        TextView tvFamilyName = view2.findViewById(R.id.tv_family_name);

        ImageView copyIcon = view2.findViewById(R.id.copyIcon);

        copyIcon.setOnClickListener(v -> copyToClipboard(tvFamilyCode.getText().toString()));

        tvFamilyCode.setOnClickListener(v -> copyToClipboard(tvFamilyCode.getText().toString()));

        createFamily2.setEnabled(false);

        familyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isValid = s.toString().trim().matches("[A-Za-zÀ-ÿ]+");
                createFamily2.setEnabled(isValid);
            }

            @Override
            public void afterTextChanged(Editable s) { //empty
            }
        });

        tvFamilyCode.setVisibility(View.GONE);
        copyIcon.setVisibility(View.GONE);
        tvFamilyName.setVisibility(View.GONE);
        newFam.setVisibility(View.GONE);


        createFamily2.setOnClickListener(v -> {
            displayFamilyJoin = false;
            String familyNameStr = familyName.getText().toString();
            codeFamily = "0000000";
            createFamily2.setVisibility(View.GONE);
            familyName.setVisibility(View.GONE);
            tvFamilyName.setVisibility(View.VISIBLE);
            tvFamilyName.setText(familyNameStr);
            tvFamilyCode.setVisibility(View.VISIBLE);
            copyIcon.setVisibility(View.VISIBLE);
            newFam.setVisibility(View.VISIBLE);

            FamilyService familyService = ApiClient.getClient(this).create(FamilyService.class);

            Call<CreateFamily> call = familyService.createFamily("Bearer " + tokenManager.getAccessToken(), new CreateFamilyBody(familyNameStr));
            call.enqueue(new Callback<CreateFamily>() {
                @Override
                public void onResponse(Call<CreateFamily> call, Response<CreateFamily> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        codeFamily = response.body().getCode();
                        tvFamilyCode.setText(codeFamily);
                        createMember(codeFamily);

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<CreateFamily> call, Throwable t) {
                }
            });


            //Supprime les boutons parents
            createFamily.setVisibility(View.GONE);
            joinFamily.setVisibility(View.GONE);
            check.setVisibility(View.VISIBLE);
            ((Animatable) check.getDrawable()).start();

        });

        bottomSheetDialog.setContentView(view2);
        bottomSheetDialog.show();
    }


    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text); // Création du ClipData
        clipboard.setPrimaryClip(clip);

    }

}
