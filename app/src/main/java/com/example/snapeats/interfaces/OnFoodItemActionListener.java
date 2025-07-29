package com.example.snapeats.interfaces;

import com.example.snapeats.models.Food_Item_Model;

public interface OnFoodItemActionListener {
    void onAddToCart(Food_Item_Model model);
    void onToggleWishlist(Food_Item_Model model, int position);
    void onFoodItemClick(Food_Item_Model model);
}
