package com.gfaim.activities.groceries;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.gfaim.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final ShoppingAdapter adapter;
    private final Drawable deleteIcon;
    private final ColorDrawable background;
    private final Paint textPaint;
    private final int iconMargin;

    public SwipeToDeleteCallback(Context context, ShoppingAdapter adapter) {
        super(0, ItemTouchHelper.LEFT); // Swipe à gauche uniquement
        this.adapter = adapter;
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete); // Remplace par ton icône
        background = new ColorDrawable(Color.RED);
        iconMargin = 40;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setFakeBoldText(true);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Pas de déplacement, juste du swipe
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.removeItem(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        // Dessine le fond rouge
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(canvas);

        // Positionne l'icône et le texte
        int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
        int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
        int iconRight = itemView.getRight() - iconMargin;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        deleteIcon.draw(canvas);

        // Dessine le texte "Supprimer"
        canvas.drawText("Supprimer", iconLeft - 200, iconBottom - 10, textPaint);

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
