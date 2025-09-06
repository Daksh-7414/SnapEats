package com.example.snapeats.managers;

import android.util.Log;
import android.widget.TextView;

import com.example.snapeats.R;
import com.example.snapeats.models.Food_Item_Model;
import com.example.snapeats.repository.FoodRepository;

import java.util.ArrayList;

public class CartManager {
    private static CartManager instance;
    FoodRepository foodRepository;

    static final int deliveryfeevalue = 15;
    static final int platformfeevalue = 5;
    TextView pricesummary, deliveryfee, platformfee, totalprice, finalprice;

    public CartManager() {
        foodRepository = new FoodRepository();
    }
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Add item to cart
    public void addToCart(Food_Item_Model food) {
        food.setInCart(true);
        food.setCart_count(food.getCart_count() + 1);
        foodRepository.updateCartFoodByItemId(food.getId(), true, food.getCart_count());
    }
    public void cartIncrement(Food_Item_Model food){
        food.setCart_count(food.getCart_count()+1);
        foodRepository.updateCartFoodByItemId(food.getId(), true, food.getCart_count());
    }
    public void cartDecrement(Food_Item_Model food){
        food.setCart_count(food.getCart_count()-1);
        foodRepository.updateCartFoodByItemId(food.getId(), true, food.getCart_count());
    }
    public void cartRemove(Food_Item_Model food){
        food.setCart_count(0);
        food.setInCart(false);
        foodRepository.updateCartFoodByItemId(food.getId(), food.isInCart(), food.getCart_count());
    }
    public int calculateTotalPrice(ArrayList<Food_Item_Model> list){
        int total = 0;

        for (Food_Item_Model item : list) {
            int itemPrice = item.getPrice(); // maan lo model me getPrice() hai
            int quantity = item.getCart_count();
            total += itemPrice * quantity;
        }

        return total;
    }

}
