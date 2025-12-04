package com.example.snapeats.data.repository;

import android.util.Log;

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
                .addValueEventListener(listener);
    }

    // ✅ Fetch wishlist foods
    public void fetchWishlistFoods(ValueEventListener listener) {
        foodRef.orderByChild("isInWishlist").equalTo(true)
                .addValueEventListener(listener);
    }

    // ✅ Fetch cart foods
    public void fetchCartFoods(ValueEventListener listener) {
//        DatabaseReference foodRefe = FirebaseDatabase.getInstance().getReference("foods");
        foodRef.orderByChild("isInCart").equalTo(true)
                .addValueEventListener(listener);
    }

    //Update the cart list in firebase
    public void updateCartFoodByItemId(String itemId, boolean inCart, int cartCount) {
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
            public void onDataChange(DataSnapshot foodsSnap) {
                boolean itemFound = false;

                for (DataSnapshot foodSnap : foodsSnap.getChildren()) { // e.g., food_01, food_02
                    String idValue = foodSnap.child("id").getValue(String.class);

                    if (idValue != null && idValue.equals(itemId)) {
                        DatabaseReference itemRef = foodSnap.getRef();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("isInCart", inCart);
                        updates.put("cart_count", cartCount);

                        itemRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("CartUpdate", "✅ Updated: " + itemId + " in " + foodSnap.getKey()))
                                .addOnFailureListener(e ->
                                        Log.e("CartUpdate", "❌ Failed to update " + itemId, e));

                        itemFound = true;
                        break;
                    }
                }

                if (!itemFound) {
                    Log.w("CartUpdate", "⚠️ Item not found with id: " + itemId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CartUpdate", "❌ Firebase error: " + error.getMessage());
            }
        });
    }

    public void updateWishlistFoodByItemId(String itemId, boolean inWishlist){
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot foodsSnap) {
                boolean itemFound = false;

                for (DataSnapshot foodSnap : foodsSnap.getChildren()) { // e.g., food_01, food_02
                    String idValue = foodSnap.child("id").getValue(String.class);

                    if (idValue != null && idValue.equals(itemId)) {
                        DatabaseReference itemRef = foodSnap.getRef();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("isInWishlist", inWishlist);
                        //updates.put("cart_count", cartCount);

                        itemRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("CartUpdate", "✅ Updated: " + itemId + " in " + foodSnap.getKey()))
                                .addOnFailureListener(e ->
                                        Log.e("CartUpdate", "❌ Failed to update " + itemId, e));

                        itemFound = true;
                        break;
                    }
                }

                if (!itemFound) {
                    Log.w("Wishlist update", "⚠️ Item not found with id: " + itemId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CartUpdate", "❌ Firebase error: " + error.getMessage());
            }
        });
    }




}
