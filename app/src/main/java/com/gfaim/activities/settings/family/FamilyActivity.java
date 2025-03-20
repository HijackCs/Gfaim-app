package com.gfaim.activities.settings.family;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gfaim.R;
import com.gfaim.activities.settings.SettingsActivity;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.family.LeaveFamilyBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnFamilyReceivedListener;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FamilyActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_MEMBER = 1;
    private GridLayout membersGrid;
    private ImageButton btnAddMember;
    private final List<MemberSessionBody> membersList = new ArrayList<>(); // Stocke les noms des membres

    UtileProfile utileProfile;
    private MemberSessionBody memberSession;
    private FamilyBody family;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family);

        utileProfile = new UtileProfile(this);

        membersGrid = findViewById(R.id.membersGrid);

        context = this;

        getAndSetInfo();
        setupLeaveFamily();
        setupBackBtn();
    }

    public void setupLeaveFamily(){
        TextView leave = findViewById(R.id.btnLeaveFamily);
        leave.setOnClickListener(v -> showLeaveConfirmationDialog());
    }

    public void getAndSetInfo(){
        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {

            }

            @Override
            public void onSuccess(MemberSessionBody session) {
                memberSession = session; // Stocke dans l'Activity
                utileProfile.getFamily(new OnFamilyReceivedListener() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onSuccess(LeaveFamilyBody family) {}
                    @Override
                    public void onSuccess(CreateFamilyBody family) {}
                    @Override
                    public void onSuccess(FamilyBody session) {
                        family = session;
                        TextView familyName = findViewById(R.id.familyName);
                        familyName.setText(family.getName());
                        TextView familyCode = findViewById(R.id.familyCode);
                        familyCode.setText(family.getCode());
                        familyCode.setOnClickListener(v -> copyToClipboard(familyCode.getText().toString()));
                        List<MemberSessionBody> list = family.getMembers();
                        if(Objects.equals(memberSession.getRole(), "CHEF")){
                            initAddButton();
                        }
                        for(MemberSessionBody m : list){
                            addMember(m);
                        }
                    }
                    @Override
                    public void onFailure(Throwable error) {
                        System.err.println("Erreur lors de la récupération de la famille : " + error.getMessage());
                    }
                }, memberSession.getFamilyId());
            }
            @Override
            public void onFailure(Throwable error) {
                System.err.println("Erreur lors de la récupération de la session : " + error.getMessage());
            }
            @Override
            public void onSuccess(CreateMember body) {}
        });
    }

    public void setupBackBtn(){
        ImageView myAccount = findViewById(R.id.back);
        myAccount.setOnClickListener(view -> {
            Intent intent = new Intent(FamilyActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    private void initAddButton(){

        btnAddMember = new ImageButton(context);

        FrameLayout editName = findViewById(R.id.editName);
        editName.setVisibility(VISIBLE);

        //TextView btnDeleteFamily = findViewById(R.id.btnDeleteFamily);
        //btnDeleteFamily.setVisibility(VISIBLE);

        btnAddMember.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        btnAddMember.setImageResource(R.drawable.ic_add);
        btnAddMember.setBackground(null);

        updateAddButtonPosition();
        setupEditBtn();

        btnAddMember.setOnClickListener(v -> {

            finish();
            Intent intent = new Intent(this, NewMemberActivity.class);
            startActivity(intent);
        });
    }

    private void addMember(MemberSessionBody member) {
        membersList.add(member);

        LinearLayout memberLayout = new LinearLayout(this);
        memberLayout.setOrientation(LinearLayout.VERTICAL);
        memberLayout.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(64, 64, 64, 64);
        memberLayout.setLayoutParams(params);

        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        frameLayout.setLayoutParams(frameParams);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(220, 220);
        imageParams.setMargins(0, 0, 0, 24);
        imageView.setLayoutParams(imageParams);
        imageView.setImageResource(R.drawable.avatar);

        frameLayout.addView(imageView);

        if(Objects.equals(memberSession.getRole(), "CHEF")) {
            if(!Objects.equals(member.getRole(), "CHEF")){
                ImageButton deleteButton = new ImageButton(this);
                FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(80, 80);
                deleteParams.gravity = Gravity.TOP | Gravity.END;
                deleteButton.setLayoutParams(deleteParams);
                deleteButton.setImageResource(R.drawable.ic_delete);
                deleteButton.setBackground(null);
                deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(member, memberLayout));
                frameLayout.addView(deleteButton);
            }else{
                ImageView grandPrince = new ImageView(this);
                FrameLayout.LayoutParams princeParams = new FrameLayout.LayoutParams(80, 80);
                princeParams.gravity = Gravity.TOP | Gravity.END;
                grandPrince.setLayoutParams(princeParams);
                grandPrince.setImageResource(R.drawable.chess_king);
                grandPrince.setBackground(null);
                frameLayout.addView(grandPrince);
            }

        }

        TextView textView = new TextView(this);
        textView.setMaxWidth(600);
        textView.setTextSize(20);
        textView.setSingleLine(false);
        textView.setEllipsize(null);
        textView.setMaxLines(Integer.MAX_VALUE);
        textView.setText(member.getFirstName());
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);



        memberLayout.addView(frameLayout);
        memberLayout.addView(textView);

        membersGrid.addView(memberLayout);
        if(Objects.equals(memberSession.getRole(), "CHEF")) {
            updateAddButtonPosition();
        }
    }


    private void showDeleteConfirmationDialog(MemberSessionBody member, LinearLayout memberLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Member")
                .setMessage("Are you sure you want to remove " + member.getFirstName() + " from the family?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    utileProfile.leaveFamily(new OnFamilyReceivedListener() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onSuccess(LeaveFamilyBody family) {}
                        @Override
                        public void onSuccess(CreateFamilyBody family) {}
                        @Override
                        public void onSuccess(FamilyBody family) {}
                        @Override
                        public void onFailure(Throwable error) {
                            membersGrid.removeView(memberLayout);
                            membersList.remove(member);
                            updateAddButtonPosition();
                        }
                    }, memberSession.getFamilyId(), member.getId());

                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.appBackground)
        )));

        alertDialog.show();
    }

    private void showLeaveConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Member")
                .setMessage("Are you sure you want to Leave the family" + family.getName())
                .setPositiveButton("Yes", (dialog, which) -> {

                    utileProfile.leaveFamily(new OnFamilyReceivedListener() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onSuccess(LeaveFamilyBody family) {}
                        @Override
                        public void onSuccess(CreateFamilyBody family) {}
                        @Override
                        public void onSuccess(FamilyBody family) {}
                        @Override
                        public void onFailure(Throwable error) {
                            Intent intent = new Intent(FamilyActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }
                    }, memberSession.getFamilyId(), memberSession.getId());
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(this, R.color.appBackground)
        )));
        alertDialog.show();
    }

    private void updateAddButtonPosition() {
        membersGrid.removeView(btnAddMember);

        int columnCount = 2;
        int totalMembers = membersList.size();

        int row = totalMembers / columnCount;
        int column = totalMembers % columnCount;


        if (column == 0 && totalMembers > 0) {
            row++;
            column = 0;
        }

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setMargins(64, 64, 64, 64);

        btnAddMember.setLayoutParams(params);
        membersGrid.addView(btnAddMember);
    }

    private void setupEditBtn(){
        EditText familyName = findViewById(R.id.familyName);
        FrameLayout editName = findViewById(R.id.editName);
        ImageView checkName = findViewById(R.id.checkName);
        ImageView cancelName = findViewById(R.id.cancelName);

        final String[] oldName = {familyName.getText().toString()}; // Stocker l'ancien nom

        familyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filteredText = s.toString().replaceAll("[^a-zA-ZÀ-ÿ ]", "");

                if (filteredText.length() > 30) {
                    filteredText = filteredText.substring(0, 30);
                }

                if (!filteredText.equals(s.toString())) {
                    familyName.setText(filteredText);
                    familyName.setSelection(filteredText.length()); // Remettre le curseur à la fin
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editName.setOnClickListener(v -> {
            oldName[0] = familyName.getText().toString();

            familyName.setFocusableInTouchMode(true);
            familyName.setCursorVisible(true);
            familyName.requestFocus();

            editName.setVisibility(GONE);
            checkName.setVisibility(VISIBLE);
            cancelName.setVisibility(VISIBLE);
        });

        //Save du nom
        checkName.setOnClickListener(v -> {
            String newName = familyName.getText().toString().trim();



            if (newName.isEmpty()) {
                familyName.setError(getString(R.string.notEmpty));
                return;
            }else{
                utileProfile.updateFamily(new OnFamilyReceivedListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onSuccess(LeaveFamilyBody family) {

                    }

                    @Override
                    public void onSuccess(CreateFamilyBody family) {

                        utileProfile.getFamily(new OnFamilyReceivedListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onSuccess(LeaveFamilyBody family) {

                            }

                            @Override
                            public void onSuccess(CreateFamilyBody family) {
                            }

                            @Override
                            public void onSuccess(FamilyBody family) {
                                TextView familyName = findViewById(R.id.familyName);
                                familyName.setText(family.getName());
                            }

                            @Override
                            public void onFailure(Throwable error) {

                            }
                        }, memberSession.getFamilyId());
                    }

                    @Override
                    public void onSuccess(FamilyBody family) {

                    }

                    @Override
                    public void onFailure(Throwable error) {
                        System.err.println("Erreur lors de la récupération de la famille : " + error.getMessage());
                    }
                }, memberSession.getFamilyId(), newName);

                familyName.setFocusable(false);
                familyName.setCursorVisible(false);

                editName.setVisibility(VISIBLE);
                checkName.setVisibility(GONE);
                cancelName.setVisibility(GONE);

            }
        });


        cancelName.setOnClickListener(v -> {
            familyName.setText(oldName[0]); // Restaurer l'ancien texte

            // Désactiver l'édition
            familyName.setFocusable(false);
            familyName.setCursorVisible(false);

            // Réafficher le bouton d'édition
            editName.setVisibility(VISIBLE);
            checkName.setVisibility(GONE);
            cancelName.setVisibility(GONE);
        });

        familyName.setOnClickListener(v -> {
            if (!familyName.isFocusable()) {
                editName.performClick();
            }
        });
    }
}
