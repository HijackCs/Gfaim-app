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
        membersList.add(name);

        // Conteneur du membre
        LinearLayout memberLayout = new LinearLayout(this);
        memberLayout.setOrientation(LinearLayout.VERTICAL);
        memberLayout.setGravity(Gravity.CENTER);

        // Augmenter l'espacement entre les membres
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(48, 48, 48, 48); // Espacement augmenté
        memberLayout.setLayoutParams(params);

        // Image du membre
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(180, 180); // Image légèrement plus grande
        imageView.setLayoutParams(imageParams);
        imageView.setImageResource(R.drawable.avatar);

        // Nom du membre
        TextView textView = new TextView(this);
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18); // Texte légèrement plus grand
        textView.setPadding(0, 8, 0, 0); // Ajout d’un petit espace sous l’image

        // Ajouter les vues au layout
        memberLayout.addView(imageView);
        memberLayout.addView(textView);

        // Ajouter le membre à la grille
        membersGrid.addView(memberLayout);

        // Met à jour la position du bouton "+"
        updateAddButtonPosition();
    }

    private void updateAddButtonPosition() {
        membersGrid.removeView(btnAddMember); // Supprime l'ancien bouton pour éviter les doublons

        int columnCount = 2; // Assurez-vous que c'est le même que dans le XML
        int totalMembers = membersList.size(); // Nombre total de membres

        int row = totalMembers / columnCount;
        int column = totalMembers % columnCount;

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);
        params.setMargins(16, 16, 16, 16);

        btnAddMember.setLayoutParams(params);
        membersGrid.addView(btnAddMember);
    }




}
