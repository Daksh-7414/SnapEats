package com.example.snapeats.data.managers;

import androidx.annotation.NonNull;

import com.example.snapeats.data.models.FoodItemModel;
import com.example.snapeats.data.repository.FoodRepository;
import com.example.snapeats.utils.SnapEatsApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class WishlistManager {
    private static WishlistManager instance;
    FoodRepository foodRepository;
    private static final HashSet<String> wishlistIds = new HashSet<>();
    private static boolean loaded = false;
    private DatabaseReference wishlistRef;

    public WishlistManager() {
        foodRepository = new FoodRepository();
    }

    public static synchronized WishlistManager getInstance() {
        if (instance == null) {
            instance = new WishlistManager();
        }
        return instance;
    }
    public void addWishlist(FoodItemModel food) {

        wishlistIds.add(food.getId());
        foodRepository.updateUserWishlistFood(food, true);
    }

    public void removeWishlist(FoodItemModel food) {
        wishlistIds.remove(food.getId());

        foodRepository.updateUserWishlistFood(food, false);
    }


    public void startWishlistListener() {

        String uid = ProfileManager.getcurrentuser().getUid();

        wishlistRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("wishlist");

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlistIds.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    wishlistIds.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    public boolean isInWishlist(String foodId) {
        return wishlistIds.contains(foodId);
    }
}
