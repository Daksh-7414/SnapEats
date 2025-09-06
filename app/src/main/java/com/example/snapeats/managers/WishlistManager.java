package com.example.snapeats.managers;

import com.example.snapeats.models.Food_Item_Model;
import com.example.snapeats.repository.FoodRepository;

public class WishlistManager {
    private static WishlistManager instance;
    FoodRepository foodRepository;

    public WishlistManager() {
        foodRepository = new FoodRepository();
    }

    public static synchronized WishlistManager getInstance() {
        if (instance == null) {
            instance = new WishlistManager();
        }
        return instance;
    }
    public void addWishlist(Food_Item_Model food){
        food.setInWishlist(true);
        foodRepository.updateWishlistFoodByItemId(food.getId(), food.isInWishlist());
    }
    public void removeWishlist(Food_Item_Model food){
        food.setInWishlist(false);
        foodRepository.updateWishlistFoodByItemId(food.getId(), food.isInWishlist());
    }
}
