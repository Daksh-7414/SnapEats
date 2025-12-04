package com.example.snapeats.data.interfaces;

import com.example.snapeats.data.models.FoodItemModel;

public interface OnCartActionListener {
    void onCartIncrement(FoodItemModel model);
    void onCartDecrement(FoodItemModel model);
    void onCartRemove(FoodItemModel model);
}
