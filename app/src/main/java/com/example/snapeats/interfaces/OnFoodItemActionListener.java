package com.example.snapeats.interfaces;

import com.example.snapeats.models.FoodItemModel;

public interface OnFoodItemActionListener {
    void onAddToCart(FoodItemModel model);
    void onToggleWishlist(FoodItemModel model, int position);
    default void onFoodItemClick(FoodItemModel model){}
}
