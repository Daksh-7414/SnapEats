package com.example.snapeats.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapeats.R;
import com.example.snapeats.adapters.PopularFoodAdapter;
import com.example.snapeats.adapters.RecommendedFoodAdapter;
import com.example.snapeats.bottomsheets.FoodDetailBottomSheet;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.models.FoodItemModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ViewPopularActivity extends AppCompatActivity {

    private RecommendedFoodAdapter popularFoodAdapter;
    RecyclerView popularFoodRecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_popular_food);

        popularFoodRecycle = findViewById(R.id.popular_food_list);

        // ✅ CHANGE TO VERTICAL LAYOUT
        popularFoodRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // ✅ FIXED: Parse as ArrayList, not single object
        ArrayList<FoodItemModel> foodList = new ArrayList<>();
        String json = getIntent().getStringExtra("PopularFoods");

        if (json != null && !json.isEmpty()) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<FoodItemModel>>(){}.getType();
                foodList = gson.fromJson(json, listType);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading food items", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }

        popularFoodAdapter = new RecommendedFoodAdapter(this, new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(ViewPopularActivity.this, "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ViewPopularActivity.this, "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(FoodItemModel model, int position) {
                if (model.isInWishlist()) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                popularFoodAdapter.notifyItemChanged(position, "wishlist");
            }

            @Override
            public void onFoodItemClick(FoodItemModel model) {
                FoodDetailBottomSheet bottomSheet = FoodDetailBottomSheet.newInstance(model);
                bottomSheet.show(ViewPopularActivity.this.getSupportFragmentManager(), "FoodDetailBottomSheet");
            }
        });

//        popularFoodAdapter.showAll = true;

        // ✅ PASS DATA TO ADAPTER
        if (foodList != null && !foodList.isEmpty()) {
            popularFoodAdapter.updateData(foodList);
        } else {
            Toast.makeText(this, "No food items to display", Toast.LENGTH_SHORT).show();
        }

        popularFoodRecycle.setAdapter(popularFoodAdapter);
    }
}