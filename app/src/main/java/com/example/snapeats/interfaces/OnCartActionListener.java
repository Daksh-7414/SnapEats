package com.example.snapeats.interfaces;

import com.example.snapeats.models.Food_Item_Model;

public interface OnCartActionListener {
    void onCartIncrement(Food_Item_Model model);
    void onCartDecrement(Food_Item_Model model);
    void onCartRemove(Food_Item_Model model);
}
