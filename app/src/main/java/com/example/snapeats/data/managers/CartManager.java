package com.example.snapeats.data.managers;



import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.snapeats.data.models.FoodItemModel;
import com.example.snapeats.data.repository.FoodRepository;
import com.example.snapeats.utils.SnapEatsApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class CartManager {
    private static CartManager instance;
    FoodRepository foodRepository;
    private static final HashSet<String> cartIds = new HashSet<>();
    private DatabaseReference cartRef;

//    static final int deliveryfeevalue = 15;
//    static final int platformfeevalue = 5;
//    TextView pricesummary, deliveryfee, platformfee, totalprice, finalprice;

    public CartManager() {
        foodRepository = new FoodRepository();
    }
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void startCartListener() {

        String uid = ProfileManager.getcurrentuser().getUid();

        cartRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("cart");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartIds.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    cartIds.add(snap.getKey()); // foodId
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void clearUserCart() {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference cartRef = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("cart");

        cartRef.removeValue()
                .addOnSuccessListener(aVoid ->
                        Log.d("Cart", "🗑 Cart cleared"))
                .addOnFailureListener(e ->
                        Log.e("Cart", "❌ Cart clear failed", e));
    }


    public boolean isInCart(String foodId) {
        return cartIds.contains(foodId);
    }

    // Add item to cart
    public void addToCart(FoodItemModel food) {
        //food.setInCart(true);
        cartIds.add(food.getId());
        //food.setCart_count(food.getCart_count() + 1);
        foodRepository.updateUserCartFood(food, 1);
    }
    public void cartIncrement(FoodItemModel food){
        food.setCart_count(food.getCart_count()+1);
        foodRepository.updateUserCartFood(food, food.getCart_count());
    }
    public void cartDecrement(FoodItemModel food){
        food.setCart_count(food.getCart_count()-1);
        foodRepository.updateUserCartFood(food, food.getCart_count());
    }
    public void cartRemove(FoodItemModel food){
        //food.setCart_count(0);
        //food.setInCart(false);
        foodRepository.updateUserCartFood(food, 0);
    }
    public int calculateTotalPrice(ArrayList<FoodItemModel> list){
        int total = 0;

        for (FoodItemModel item : list) {
            int itemPrice = item.getPrice(); // maan lo model me getPrice() hai
            int quantity = item.getCart_count();
            total += itemPrice * quantity;
        }

        return total;
    }

}
