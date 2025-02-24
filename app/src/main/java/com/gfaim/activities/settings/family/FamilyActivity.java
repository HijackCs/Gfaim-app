package com.gfaim.activities.settings.family;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gfaim.R;

import java.util.ArrayList;
import java.util.List;

public class FamilyActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_MEMBER = 1;

    private GridLayout membersGrid;
    private ImageButton btnAddMember;
    private List<String> membersList = new ArrayList<>(); // Stocke les noms des membres

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

        // Ajouter le bouton "+"
        updateAddButtonPosition();

        initAddButton();
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

        // Ajoute le membre à la liste
        membersList.add(name);

        // Création d'un conteneur pour le membre
        LinearLayout memberLayout = new LinearLayout(this);
        memberLayout.setOrientation(LinearLayout.VERTICAL);
        memberLayout.setGravity(Gravity.CENTER);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(16, 16, 16, 16);
        memberLayout.setLayoutParams(params);

        // Ajout d'une image pour le membre
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150));
        imageView.setImageResource(R.drawable.ic_person); // Remplace avec ton icône

        // Ajout du nom du membre
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);

        // Ajout des vues dans le layout du membre
        memberLayout.addView(imageView);
        memberLayout.addView(textView);

        // Ajout du membre au GridLayout
        membersGrid.addView(memberLayout);

        // Met à jour la position du bouton "+"
        updateAddButtonPosition();
    }

    private void updateAddButtonPosition() {
        // Retirer le bouton + s'il existe déjà
        membersGrid.removeView(btnAddMember);

        // Ajouter le bouton + après le dernier membre
        membersGrid.addView(btnAddMember);
    }
}
