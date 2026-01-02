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

//    ✅ Fetch all categories
    public void fetchCategories(ValueEventListener listener) {
        categoryRef.addListenerForSingleValueEvent(listener);
    }

    // ✅ Fetch all foods
    public void fetchAllFoods(ValueEventListener listener) {
        foodRef.addListenerForSingleValueEvent(listener);
    }

    // ✅ Fetch popular foods
    public void fetchPopularFoods(ValueEventListener listener) {
        foodRef.orderByChild("isPopular").equalTo(true)
                .addValueEventListener(listener);
    }

    // ✅ Fetch recommended foods
    public void fetchRecommendedFoods(ValueEventListener listener) {
        foodRef.orderByChild("isRecommended").equalTo(true)
                .addListenerForSingleValueEvent(listener);
    }

    // ✅ Fetch wishlist foods
    public void fetchWishlistFoods(ValueEventListener listener) {
        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference wishlistRef =
                SnapEatsApplication.getFirebaseDatabase()
                        .getReference("Users")
                        .child(uid)
                        .child("wishlist");

        wishlistRef.addValueEventListener(listener);
    }

    // ✅ Fetch cart foods
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


    //Update the cart list in firebase
//    public void updateCartFoodByItemId(String itemId, boolean inCart, int cartCount) {
//        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//            public void onDataChange(DataSnapshot foodsSnap) {
//                boolean itemFound = false;
//
//                for (DataSnapshot foodSnap : foodsSnap.getChildren()) { // e.g., food_01, food_02
//                    String idValue = foodSnap.child("id").getValue(String.class);
//
//                    if (idValue != null && idValue.equals(itemId)) {
//                        DatabaseReference itemRef = foodSnap.getRef();
//
//                        Map<String, Object> updates = new HashMap<>();
//                        updates.put("isInCart", inCart);
//                        updates.put("cart_count", cartCount);
//
//                        itemRef.updateChildren(updates)
//                                .addOnSuccessListener(aVoid ->
//                                        Log.d("CartUpdate", "✅ Updated: " + itemId + " in " + foodSnap.getKey()))
//                                .addOnFailureListener(e ->
//                                        Log.e("CartUpdate", "❌ Failed to update " + itemId, e));
//
//                        itemFound = true;
//                        break;
//                    }
//                }
//
//                if (!itemFound) {
//                    Log.w("CartUpdate", "⚠️ Item not found with id: " + itemId);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.e("CartUpdate", "❌ Firebase error: " + error.getMessage());
//            }
//        });
//    }

//    public void updateUserCartFood(FoodItemModel food, boolean inCart, int cartCount) {
//        String uid = ProfileManager.getcurrentuser().getUid();
//
//        DatabaseReference cartItemRef = SnapEatsApplication.getFirebaseDatabase()
//                .getReference("Users")
//                .child(uid)
//                .child("cart")
//                .child(food.getId());
//
//        if (inCart) {
//            // First, check if item already exists
//            cartItemRef.get().addOnSuccessListener(snapshot -> {
//                if (snapshot.exists()) {
//                    // Item exists, update only cart_count and isInCart
//                    Map<String, Object> updates = new HashMap<>();
//                    updates.put("cart_count", cartCount);
//                    updates.put("isInCart", true);
//
//                    cartItemRef.updateChildren(updates)
//                            .addOnSuccessListener(aVoid ->
//                                    Log.d("UserCart", "✅ Existing cart item updated: " + food.getId()))
//                            .addOnFailureListener(e ->
//                                    Log.e("UserCart", "❌ Failed to update cart item", e));
//                } else {
//                    // Item does not exist, store the entire FoodItemModel object
//                    food.setCart_count(cartCount);
//                    food.setInCart(true);
//
//                    cartItemRef.setValue(food)
//                            .addOnSuccessListener(aVoid ->
//                                    Log.d("UserCart", "✅ New cart item added: " + food.getId()))
//                            .addOnFailureListener(e ->
//                                    Log.e("UserCart", "❌ Failed to add cart item", e));
//                }
//            }).addOnFailureListener(e ->
//                    Log.e("UserCart", "❌ Failed to fetch cart item", e));
//        } else {
//            // Remove from cart
//            cartItemRef.removeValue()
//                    .addOnSuccessListener(aVoid ->
//                            Log.d("UserCart", "🗑 Removed from cart: " + food.getId()))
//                    .addOnFailureListener(e ->
//                            Log.e("UserCart", "❌ Failed to remove cart item", e));
//        }
//    }

    public void updateUserCartFood(FoodItemModel food, int cartCount) {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference cartItemRef = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("cart")
                .child(food.getId());

        if (cartCount > 0) {

            // update model quantity
            food.setCart_count(cartCount);

            // 🔥 FULL OBJECT STORE (merge by key)
            cartItemRef.setValue(food)
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserCart", "✅ Cart item saved/updated: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserCart", "❌ Failed to save cart item", e));

        } else {
            // 🔥 remove if quantity = 0
            cartItemRef.removeValue()
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserCart", "🗑 Removed from cart: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserCart", "❌ Failed to remove cart item", e));
        }
    }



    //    public void updateWishlistFoodByItemId(String itemId, boolean inWishlist){
    //        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
    //            @Override
    //            public void onDataChange(DataSnapshot foodsSnap) {
    //                boolean itemFound = false;
    //
    //                for (DataSnapshot foodSnap : foodsSnap.getChildren()) { // e.g., food_01, food_02
    //                    String idValue = foodSnap.child("id").getValue(String.class);
    //
    //                    if (idValue != null && idValue.equals(itemId)) {
    //                        DatabaseReference itemRef = foodSnap.getRef();
    //
    //                        Map<String, Object> updates = new HashMap<>();
    //                        updates.put("isInWishlist", inWishlist);
    //
    //                        itemRef.updateChildren(updates)
    //                                .addOnSuccessListener(aVoid ->
    //                                        Log.d("CartUpdate", "✅ Updated: " + itemId + " in " + foodSnap.getKey()))
    //                                .addOnFailureListener(e ->
    //                                        Log.e("CartUpdate", "❌ Failed to update " + itemId, e));
    //
    //                        itemFound = true;
    //                        break;
    //                    }
    //                }
    //
    //                if (!itemFound) {
    //                    Log.w("Wishlist update", "⚠️ Item not found with id: " + itemId);
    //                }
    //            }
    //
    //            @Override
    //            public void onCancelled(DatabaseError error) {
    //                Log.e("CartUpdate", "❌ Firebase error: " + error.getMessage());
    //            }
    //        });
    //    }

    public void updateUserWishlistFood(FoodItemModel food, boolean addToWishlist) {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference wishlistRef = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("wishlist")
                .child(food.getId());

        if (addToWishlist) {

            // ✅ store FULL FOOD MODEL (NO BOOLEAN)
            wishlistRef.setValue(food)
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserWishlist", "❤️ Added to wishlist: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserWishlist", "❌ Failed to add wishlist", e));

        } else {

            // ✅ remove item
            wishlistRef.removeValue()
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserWishlist", "🗑 Removed from wishlist: " + food.getId()))
                    .addOnFailureListener(e ->
                            Log.e("UserWishlist", "❌ Failed to remove wishlist", e));
        }
    }






}
