package com.example.snapeats.data.interfaces;

import com.example.snapeats.data.models.FoodItemModel;

public interface OnFoodItemActionListener {
    void onAddToCart(FoodItemModel model);
    void onToggleWishlist(FoodItemModel model, int position);
    default void onFoodItemClick(FoodItemModel model){}
}
