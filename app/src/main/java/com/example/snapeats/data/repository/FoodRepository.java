package com.example.snapeats.data.repository;

import android.util.Log;

import com.example.snapeats.data.managers.ProfileManager;
import com.example.snapeats.data.models.FoodItemModel;
import com.example.snapeats.utils.SnapEatsApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FoodRepository {
    private DatabaseReference foodRef;
    private DatabaseReference categoryRef;

    public FoodRepository() {
        foodRef = FirebaseDatabase.getInstance().getReference("foods");
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");
    }


    public void fetchCategories(ValueEventListener listener) {
        categoryRef.addListenerForSingleValueEvent(listener);
    }


    public void fetchAllFoods(ValueEventListener listener) {
        foodRef.addListenerForSingleValueEvent(listener);
    }


    public void fetchPopularFoods(ValueEventListener listener) {
        foodRef.orderByChild("isPopular").equalTo(true)
                .addValueEventListener(listener);
    }

    public void fetchRecommendedFoods(ValueEventListener listener) {
        foodRef.orderByChild("isRecommended").equalTo(true)
                .addListenerForSingleValueEvent(listener);
    }


    public void fetchWishlistFoods(ValueEventListener listener) {
        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference wishlistRef =
                SnapEatsApplication.getFirebaseDatabase()
                        .getReference("Users")
                        .child(uid)
                        .child("wishlist");

        wishlistRef.addValueEventListener(listener);
    }


    public void fetchCartFoods(ValueEventListener listener) {
        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference cartRef =
                SnapEatsApplication.getFirebaseDatabase()
                        .getReference("Users")
                        .child(uid)
                        .child("cart");

        cartRef.addValueEventListener(listener);
    }

    public void fetchOrderedFoods(ValueEventListener listener) {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference ordersRef =
                SnapEatsApplication.getFirebaseDatabase()
                        .getReference("Users")
                        .child(uid)
                        .child("orders");

        ordersRef.addValueEventListener(listener);
    }

    public void updateUserCartFood(FoodItemModel food, int cartCount) {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference cartItemRef = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("cart")
                .child(food.getId());

        if (cartCount > 0) {


            food.setCart_count(cartCount);


            cartItemRef.setValue(food)
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserCart", " Cart item saved/updated: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserCart", " Failed to save cart item", e));

        } else {

            cartItemRef.removeValue()
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserCart", "Removed from cart: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserCart", " Failed to remove cart item", e));
        }
    }


    public void updateUserWishlistFood(FoodItemModel food, boolean addToWishlist) {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference wishlistRef = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("wishlist")
                .child(food.getId());

        if (addToWishlist) {

            wishlistRef.setValue(food)
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserWishlist", " Added to wishlist: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserWishlist", " Failed to add wishlist", e));

        } else {

            // ✅ remove item
            wishlistRef.removeValue()
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserWishlist", "Removed from wishlist: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserWishlist", " Failed to remove wishlist", e));
        }
    }






}
