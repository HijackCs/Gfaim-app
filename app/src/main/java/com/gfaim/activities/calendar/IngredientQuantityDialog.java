package com.gfaim.activities.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gfaim.R;
import com.gfaim.activities.calendar.model.Ingredient;

public class IngredientQuantityDialog extends Dialog {
    private final Ingredient ingredient;
    private final OnIngredientQuantityListener listener;

    public interface OnIngredientQuantityListener {
        void onIngredientQuantitySelected(Ingredient ingredient, double quantity, String unit);
    }

    public IngredientQuantityDialog(Context context, Ingredient ingredient, OnIngredientQuantityListener listener) {
        super(context);
        this.ingredient = ingredient;
        this.listener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_ingredient_quantity);

        // Configuration du titre (nom de l'ingrédient)
        TextView ingredientNameTextView = findViewById(R.id.ingredient_name);
        ingredientNameTextView.setText(ingredient.getName());

        // Configuration du spinner d'unités
        Spinner unitSpinner = findViewById(R.id.unit_spinner);
        String[] units = {"g", "ml", "unité(s)", "cuillère(s) à soupe", "cuillère(s) à café"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, units);
        unitSpinner.setAdapter(adapter);

        // Champ de quantité
        EditText quantityEditText = findViewById(R.id.quantity_edit_text);

        // Bouton de validation
        Button validateButton = findViewById(R.id.validate_button);
        validateButton.setOnClickListener(v -> {
            String quantityStr = quantityEditText.getText().toString();
            if (quantityStr.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez entrer une quantité", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityStr);
                String unit = unitSpinner.getSelectedItem().toString();

                // Appeler le listener avec les informations sélectionnées
                if (listener != null) {
                    listener.onIngredientQuantitySelected(ingredient, quantity, unit);
                }

                // Fermer le dialog
                dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Veuillez entrer une quantité valide", Toast.LENGTH_SHORT).show();
            }
        });
    }
}