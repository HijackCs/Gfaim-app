package com.gfaim.activities.settings.family;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

public class FamilyActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_MEMBER = 1;

    private GridLayout membersGrid;
    private ImageButton btnAddMember;
    private final List<String> membersList = new ArrayList<>(); // Stocke les noms des membres

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family);

        membersGrid = findViewById(R.id.membersGrid);
        btnAddMember = new ImageButton(this);
        btnAddMember.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        btnAddMember.setImageResource(R.drawable.ic_add);
        btnAddMember.setBackground(null);

        // Ajouter des membres initiaux
        addMember("John");
        addMember("Alice");

        TextView familyCode = findViewById(R.id.familyCode);
        familyCode.setOnClickListener(v -> copyToClipboard(familyCode.getText().toString()));



        updateAddButtonPosition();

        initAddButton();

        setupEditBtn();

    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    private void initAddButton(){
        btnAddMember.setOnClickListener(v -> {
            Intent intent = new Intent(FamilyActivity.this, NewMemberActivity.class);
            startActivityForResult(intent, REQUEST_ADD_MEMBER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_MEMBER && resultCode == RESULT_OK) {
            String newMemberName = data.getStringExtra("MEMBER_NAME");
            if (newMemberName != null) {
                addMember(newMemberName);
            }
        }
    }

    private void addMember(String name) {
        membersList.add(name);

        LinearLayout memberLayout = new LinearLayout(this);
        memberLayout.setOrientation(LinearLayout.VERTICAL);
        memberLayout.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(64, 64, 64, 64);
        memberLayout.setLayoutParams(params);

        // 📌 FrameLayout pour superposer l’image et le bouton
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

        // ❌ Bouton de suppression superposé
        ImageButton deleteButton = new ImageButton(this);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(80, 80);
        deleteParams.gravity = Gravity.TOP | Gravity.END; // Coin supérieur droit
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(R.drawable.ic_delete);
        deleteButton.setBackground(null);
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(name, memberLayout));

        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        textView.setPadding(8, 8, 8, 8);
        textView.setMaxWidth(280);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(false);
        textView.setMaxLines(2);

        // Ajout des éléments dans le FrameLayout (image + bouton)
        frameLayout.addView(imageView);
        frameLayout.addView(deleteButton);

        memberLayout.addView(frameLayout);
        memberLayout.addView(textView);

        membersGrid.addView(memberLayout);
        updateAddButtonPosition();
    }


    private void showDeleteConfirmationDialog(String name, LinearLayout memberLayout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Member")
                .setMessage("Are you sure you want to remove " + name + " from the family?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    membersGrid.removeView(memberLayout);
                    membersList.remove(name);
                    updateAddButtonPosition();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();

        // 🌟 Changer la couleur de fond
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

            editName.setVisibility(View.GONE);
            checkName.setVisibility(View.VISIBLE);
            cancelName.setVisibility(View.VISIBLE);
        });

        //Save du nom
        checkName.setOnClickListener(v -> {
            String newName = familyName.getText().toString().trim();

            if (newName.isEmpty()) {
                familyName.setError(getString(R.string.notEmpty));
                return;
            }

            familyName.setFocusable(false);
            familyName.setCursorVisible(false);

            editName.setVisibility(View.VISIBLE);
            checkName.setVisibility(View.GONE);
            cancelName.setVisibility(View.GONE);
        });

        cancelName.setOnClickListener(v -> {
            familyName.setText(oldName[0]); // Restaurer l'ancien texte

            // Désactiver l'édition
            familyName.setFocusable(false);
            familyName.setCursorVisible(false);

            // Réafficher le bouton d'édition
            editName.setVisibility(View.VISIBLE);
            checkName.setVisibility(View.GONE);
            cancelName.setVisibility(View.GONE);
        });

        familyName.setOnClickListener(v -> {
            if (!familyName.isFocusable()) {
                editName.performClick();
            }
        });
    }
}
