package com.example.snapeats.managers;

import com.example.snapeats.models.FoodItemModel;
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
    public void addWishlist(FoodItemModel food){
        food.setInWishlist(true);
        foodRepository.updateWishlistFoodByItemId(food.getId(), food.isInWishlist());
    }
    public void removeWishlist(FoodItemModel food){
        food.setInWishlist(false);
        foodRepository.updateWishlistFoodByItemId(food.getId(), food.isInWishlist());
    }
}
