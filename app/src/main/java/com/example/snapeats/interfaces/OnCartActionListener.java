package com.example.snapeats.interfaces;

import com.example.snapeats.models.FoodItemModel;

public interface OnCartActionListener {
    void onCartIncrement(FoodItemModel model);
    void onCartDecrement(FoodItemModel model);
    void onCartRemove(FoodItemModel model);
}
