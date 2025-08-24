package com.example.snapeats.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.snapeats.models.Food_Item_Model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * Repository class for managing food data from Firebase.
 * Fetches all food items into a master list and filters them into categorized lists.
 * Use getters to access data in UI components.
 */
public class FoodRepository {
    // Master list of all fetched foods (single source of truth)
    private static ArrayList<Food_Item_Model> allFoods = new ArrayList<>();

    // Filtered lists (derived from allFoods)
    public static ArrayList<Food_Item_Model> popularFoods = new ArrayList<>();
    public static ArrayList<Food_Item_Model> recommendedFoods = new ArrayList<>();
    public static ArrayList<Food_Item_Model> wishlistFoods = new ArrayList<>();
    public static ArrayList<Food_Item_Model> cartFoods = new ArrayList<>();

    /**
     * Fetches food data from Firebase once and updates filtered lists.
     * @param onComplete Callback to run after successful fetch (e.g., for UI refresh).
     */
    public static void fetchFoods(Runnable onComplete) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("foods"); // Adjust path if needed
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allFoods.clear();
                if (!snapshot.exists()) {
                    Log.e("FoodRepository", "No data found at path 'food'");
                    if (onComplete != null) onComplete.run(); // Still call for empty handling
                    return;
                }
                for (DataSnapshot foodSnap : snapshot.getChildren()) {
                    Food_Item_Model item = foodSnap.getValue(Food_Item_Model.class);
                    if (item != null) {
                        allFoods.add(item);
                    } else {
                        Log.w("FoodRepository", "Null item in snapshot: " + foodSnap.getKey());
                    }
                }
                Log.d("FoodRepository", "Fetched " + allFoods.size() + " food items");
                updateFilteredLists(); // Automatically update derived lists
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FoodRepository", "Fetch cancelled: " + error.getMessage());
            }
        });
    }

    /**
     * Filters allFoods into separate lists based on boolean flags in Food_Item_Model.
     * Assumes model has methods like isPopular(), isRecommended(), etc.
     */
    public static void updateFilteredLists() {
        popularFoods.clear();
        recommendedFoods.clear();
        wishlistFoods.clear();
        cartFoods.clear();

        for (Food_Item_Model item : allFoods) {
            if (item.isPopular()) {
                popularFoods.add(item);
            }
            if (item.isRecommended()) {
                recommendedFoods.add(item);
            }
            if (item.isInWishlist()) {
                wishlistFoods.add(item);
            }
            if (item.isInCart()) {
                cartFoods.add(item);
            }
        }
        Log.d("FoodRepository", "Filtered lists updated - Popular: " + popularFoods.size() +
                ", Recommended: " + recommendedFoods.size() +
                ", Wishlist: " + wishlistFoods.size() +
                ", Cart: " + cartFoods.size());
    }

    // Getters for accessing lists (read-only access)
    public static ArrayList<Food_Item_Model> getAllFoods() {
        return new ArrayList<>(allFoods); // Return copy for safety
    }

    public static ArrayList<Food_Item_Model> getPopularFoods() {
        return new ArrayList<>(popularFoods); // Return copy
    }

    public static ArrayList<Food_Item_Model> getRecommendedFoods() {
        return new ArrayList<>(recommendedFoods); // Return copy
    }

    public static ArrayList<Food_Item_Model> getWishlistFoods() {
        return new ArrayList<>(wishlistFoods); // Return copy
    }

    public static ArrayList<Food_Item_Model> getCartFoods() {
        return new ArrayList<>(cartFoods); // Return copy
    }

    /**
     * Updates a single item in allFoods and refreshes filtered lists.
     * @param updatedItem The item to update (must match an existing one via equals() or ID).
     */
    public static void updateItem(Food_Item_Model updatedItem) {
        for (int i = 0; i < allFoods.size(); i++) {
            // Assuming Food_Item_Model overrides equals() or has a unique ID for comparison
            if (allFoods.get(i).equals(updatedItem)) {
                allFoods.set(i, updatedItem);
                Log.d("FoodRepository", "Item updated: " + updatedItem.toString());
                break;
            }
        }
        updateFilteredLists(); // Refresh derived lists
    }
}
