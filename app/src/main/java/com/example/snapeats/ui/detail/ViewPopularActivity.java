package com.example.snapeats.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snapeats.R;
import com.example.snapeats.ui.adapters.RecommendedFoodAdapter;
import com.example.snapeats.ui.bottomsheets.FoodDetailBottomSheet;
import com.example.snapeats.data.interfaces.OnFoodItemActionListener;
import com.example.snapeats.data.managers.CartManager;
import com.example.snapeats.data.managers.WishlistManager;
import com.example.snapeats.data.models.FoodItemModel;
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

        ImageButton backArrow = findViewById(R.id.back_arrow);
        if (backArrow != null) {
            backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            });
        } else {
            Log.e("Error when click on back button", "Back button (imageButton) not found in layout!");
        }

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
                if (!CartManager.getInstance().isInCart(model.getId())) {
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(ViewPopularActivity.this, "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ViewPopularActivity.this, "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(FoodItemModel model, int position) {
                if (WishlistManager.getInstance().isInWishlist(model.getId())) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                popularFoodAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFoodItemClick(FoodItemModel model) {
                FoodDetailBottomSheet bottomSheet = FoodDetailBottomSheet.newInstance(model);
                bottomSheet.setOnFoodUpdatedListener(() -> {
                    popularFoodAdapter.notifyDataSetChanged();
                });

                bottomSheet.show(getSupportFragmentManager(), "FoodDetailBottomSheet");
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