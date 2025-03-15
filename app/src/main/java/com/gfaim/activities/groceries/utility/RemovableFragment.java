package com.gfaim.activities.groceries.utility;

import com.gfaim.models.FoodItem;

public interface RemovableFragment {
    void removeItem(FoodItem position);

    FoodItem getItemAtPosition(int position);
}
