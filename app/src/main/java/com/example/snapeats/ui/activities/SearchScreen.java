package com.example.snapeats.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

public class SearchScreen extends AppCompatActivity {

    private static final String TAG = "SearchScreen";
    private SearchView search_bar;
    private RecyclerView searchRecyclerView;
    private LinearLayout searchLayout;  // This is your "Not Found" layout
    private RecommendedFoodAdapter searchAdapter;
    private ArrayList<FoodItemModel> allFoodList;
    private ArrayList<FoodItemModel> filteredFoodList;
    private ImageButton backArrow;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        backArrow = findViewById(R.id.backArrow);
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
            Log.e(TAG, "Back button (imageButton) not found in layout!");
        }

        // Initialize views
        search_bar = findViewById(R.id.search_bar);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchLayout = findViewById(R.id.search_layout);  // Initialize the "Not Found" layout

        // Initially hide both RecyclerView and "Not Found" layout
        searchRecyclerView.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);

        // Setup RecyclerView
        setupRecyclerView();

        // Get food list from intent
        String json = getIntent().getStringExtra("FOOD_LIST_JSON");
        Log.d(TAG, "Received JSON length: " + (json != null ? json.length() : "null"));

        if (json != null && !json.isEmpty()) {
            parseFoodList(json);
        } else {
            Log.e(TAG, "JSON is null or empty!");
            allFoodList = new ArrayList<>();
            filteredFoodList = new ArrayList<>();
        }

        // Setup search functionality
        setupSearchView();

        // Initial log
        logCurrentState("After onCreate");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupRecyclerView() {
        filteredFoodList = new ArrayList<>();
        searchAdapter = new RecommendedFoodAdapter(this, new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!CartManager.getInstance().isInCart(model.getId())) {
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(SearchScreen.this, "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SearchScreen.this, "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(FoodItemModel model, int position) {
                if (WishlistManager.getInstance().isInWishlist(model.getId())) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                searchAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFoodItemClick(FoodItemModel model) {
                FoodDetailBottomSheet bottomSheet = FoodDetailBottomSheet.newInstance(model);
                bottomSheet.setOnFoodUpdatedListener(() -> {
                    searchAdapter.notifyDataSetChanged();
                });

                bottomSheet.show(getSupportFragmentManager(), "FoodDetailBottomSheet");
            }
        });
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchAdapter);
    }

    private void parseFoodList(String json) {
        try {
            Log.d(TAG, "Parsing JSON...");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FoodItemModel>>(){}.getType();
            allFoodList = gson.fromJson(json, type);

            if (allFoodList == null) {
                Log.e(TAG, "Gson returned null list");
                allFoodList = new ArrayList<>();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing food list: " + e.getMessage(), e);
            allFoodList = new ArrayList<>();
            filteredFoodList = new ArrayList<>();
        }
    }

    private void setupSearchView() {
        search_bar.clearFocus();
        search_bar.requestFocus();

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search submitted: " + query);
                filterList(query);
                search_bar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Search text changed: " + newText);
                if (newText.isEmpty()) {

                    searchRecyclerView.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);
                    Log.d(TAG, "Search cleared - hiding both views");
                } else {

                    filterList(newText);
                }
                return true;
            }
        });
    }

    private void filterList(String text) {
        Log.d(TAG, "Filtering with text: '" + text + "'");
        Log.d(TAG, "All food list size: " + allFoodList.size());

        ArrayList<FoodItemModel> newFilteredList = new ArrayList<>();

        if (text.isEmpty()) {

            searchRecyclerView.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            Log.d(TAG, "Text empty - hiding both views");
            return;
        } else {
            String searchText = text.toLowerCase().trim();
            Log.d(TAG, "Searching for: " + searchText);

            for (FoodItemModel food : allFoodList) {
                boolean matches = false;

                // Check food name (case insensitive)
                if (food.getFood_name() != null &&
                        food.getFood_name().toLowerCase().contains(searchText)) {
                    matches = true;
                    Log.d(TAG, "Found in name: " + food.getFood_name());
                }
                // Also check category if you want even broader search
                else if (food.getFood_restaurant_name() != null &&
                        food.getFood_restaurant_name().toLowerCase().contains(searchText)) {
                    matches = true;
                    Log.d(TAG, "Found in category: " + food.getFood_name());
                }

                if (matches) {
                    newFilteredList.add(food);
                }
            }

            Log.d(TAG, "Found " + newFilteredList.size() + " matching items");
        }

        // Update the filtered list
        filteredFoodList.clear();
        filteredFoodList.addAll(newFilteredList);

        // Update adapter with new data
        searchAdapter.updateData(filteredFoodList);

        // Update UI state based on results
        updateUIState();

        logCurrentState("After filterList");
    }

    private void updateUIState() {
        if (filteredFoodList.isEmpty()) {
            // Show "Not Found" layout, hide RecyclerView
            searchRecyclerView.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "No results found, showing 'Not Found' layout");
        } else {
            // Show RecyclerView, hide "Not Found" layout
            searchRecyclerView.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            Log.d(TAG, "Found results, showing RecyclerView");
        }
    }

    private void logCurrentState(String context) {
        Log.d(TAG, context + " - State:");
        Log.d(TAG, "  All food list size: " + (allFoodList != null ? allFoodList.size() : "null"));
        Log.d(TAG, "  Filtered list size: " + (filteredFoodList != null ? filteredFoodList.size() : "null"));
        Log.d(TAG, "  Search bar text: " + search_bar.getQuery());
        Log.d(TAG, "  Adapter item count: " + searchAdapter.getItemCount());
        Log.d(TAG, "  RecyclerView visible: " + (searchRecyclerView.getVisibility() == View.VISIBLE));
        Log.d(TAG, "  Not Found layout visible: " + (searchLayout.getVisibility() == View.VISIBLE));
    }
}