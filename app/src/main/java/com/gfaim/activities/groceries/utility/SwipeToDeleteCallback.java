package com.gfaim.activities.groceries.utility;

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
import com.gfaim.models.FoodItem;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final RemovableFragment fragment;
    private final Drawable deleteIcon;
    private final ColorDrawable background;
    private final Paint textPaint;
    private final int iconMargin;

    public SwipeToDeleteCallback(Context context, RemovableFragment fragment) {
        super(0, ItemTouchHelper.LEFT); // Swipe à gauche uniquement
        this.fragment = fragment;
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
        iconMargin = 40;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setFakeBoldText(true);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        FoodItem item = fragment.getItemAtPosition(position);  // Récupère l'élément à la position
        fragment.removeItem(item);  // Suppression via le fragment
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(canvas);

        int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
        int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
        int iconRight = itemView.getRight() - iconMargin;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        deleteIcon.draw(canvas);

        canvas.drawText("Supprimer", iconLeft - 200, iconBottom - 10, textPaint);

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
