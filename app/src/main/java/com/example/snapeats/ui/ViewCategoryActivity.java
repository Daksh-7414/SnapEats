package com.example.snapeats.ui;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapeats.R;
import com.example.snapeats.adapters.CategoryAdapter;
import com.example.snapeats.adapters.PopularFoodAdapter;
import com.example.snapeats.bottomsheets.FoodDetailBottomSheet;
import com.example.snapeats.fragements.NoInternetScreen;
import com.example.snapeats.interfaces.OnCategoryActionListener;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.models.CategoriesModel;
import com.example.snapeats.models.FoodItemModel;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ViewCategoryActivity extends AppCompatActivity{

    private PopularFoodAdapter popularFoodAdapter;
    FoodRepository foodRepository;
    CategoryAdapter categoryAdapter;
    RecyclerView popularFoodRecycle;

    public ArrayList<FoodItemModel> popularFoods = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_category);

        foodRepository = new FoodRepository();

        popularFoodRecycle = findViewById(R.id.popular_food_list);
        popularFoodRecycle.setLayoutManager(new GridLayoutManager(ViewCategoryActivity.this, 2));
        popularFoodAdapter = new PopularFoodAdapter(getApplicationContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(getApplicationContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
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
                bottomSheet.show(getSupportFragmentManager(), "FoodDetailBottomSheet");
            }


        });
        popularFoodRecycle.setAdapter(popularFoodAdapter);

        // RecyclerView for Categories
        RecyclerView recyclerView = findViewById(R.id.categoriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewCategoryActivity.this,RecyclerView.VERTICAL,false));
        categoryAdapter = new CategoryAdapter(ViewCategoryActivity.this, new OnCategoryActionListener() {
            @Override
            public void onCategoryClick(CategoriesModel model) {
                filterFetch(model);
            }
        });
        recyclerView.setAdapter(categoryAdapter);

        // Fetch Data from Firebase
        FetchAll();



    }

    private void FetchAll() {
        if (!NetworkUtils.isInternetAvailable(ViewCategoryActivity.this)) {
            if (ViewCategoryActivity.this != null) {
                ViewCategoryActivity.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new NoInternetScreen())
                        .addToBackStack(null)
                        .commit();
            }
            return;
        }
        foodRepository.fetchCategories(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<CategoriesModel> categories = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    CategoriesModel category = child.getValue(CategoriesModel.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }

                // Now update adapter
                categoryAdapter.updateData(categories);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        foodRepository.fetchAllFoods(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<FoodItemModel> popularFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) popularFoods.add(food);
                }
                popularFoodAdapter.updateData(popularFoods);
                String json = getIntent().getStringExtra("CategoryModel");
                if (json != null) {
                    Gson gson = new Gson();
                    CategoriesModel model = gson.fromJson(json, CategoriesModel.class);
                    categoryAdapter.setSelectedCategory(model.getCategoryName());
                    filterFetch(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void filterFetch(CategoriesModel model){
        ArrayList<FoodItemModel> filterList = new ArrayList<>();
        for (FoodItemModel food : popularFoods) {
            if (food.getCategory().equalsIgnoreCase(model.getCategoryName())) {
                filterList.add(food);

            }
        }
        popularFoodAdapter.updateData(filterList);
    }
}