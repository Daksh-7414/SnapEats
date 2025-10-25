package com.example.snapeats.fragements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapeats.bottomsheets.FoodDetailBottomSheet;
import com.example.snapeats.interfaces.OnCategoryActionListener;
import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.models.CategoriesModel;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.ui.FoodDetailScreen;
import com.example.snapeats.R;
import com.example.snapeats.ui.ViewCategoryActivity;
import com.example.snapeats.ui.ViewPopularActivity;
import com.example.snapeats.adapters.CategoryAdapter;
import com.example.snapeats.adapters.PopularFoodAdapter;
import com.example.snapeats.adapters.RecommendedFoodAdapter;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.FoodItemModel;
import com.example.snapeats.utils.NetworkUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class HomeScreenFragment extends Fragment {

    private CategoryAdapter categoryAdapter;
    private PopularFoodAdapter popularFoodAdapter;
    private RecommendedFoodAdapter recommendedFoodAdapter;



    private FoodRepository foodRepository;
    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodRepository = new FoodRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        ProgressBar loader ;
        NestedScrollView contentLayout ;
        loader = view.findViewById(R.id.loader);
        contentLayout = view.findViewById(R.id.contentLayout);

        // Simulate delay like network call (2 seconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);

        // Inflate the layout for this fragment
        RecyclerView recyclerView = view.findViewById(R.id.categoriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

        categoryAdapter = new CategoryAdapter(getContext(), new OnCategoryActionListener() {
            @Override
            public void onCategoryClick(CategoriesModel model) {
//                Intent intent = new Intent(getContext(), ViewCategoryActivity.class);
//                startActivity(intent);
                Gson gson = new Gson();
                String json = gson.toJson(model);
                Intent intent = new Intent(getContext(), ViewCategoryActivity.class);
                intent.putExtra("CategoryModel", json);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(categoryAdapter);

        TextView viewCategory = view.findViewById(R.id.viewCategory);
        viewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewCat = new Intent(getContext(), ViewCategoryActivity.class);
                startActivity(viewCat);
            }
        });

        TextView viewPopular = view.findViewById(R.id.viewPopularfood);
        viewPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPop = new Intent(getContext(), ViewPopularActivity.class);
                startActivity(viewPop); // <-- You missed this line
            }
        });


        RecyclerView popularFoodRecycle = view.findViewById(R.id.popular_food_list);
        popularFoodRecycle.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        popularFoodAdapter = new PopularFoodAdapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
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
                bottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "FoodDetailBottomSheet");
            }

        });
        popularFoodRecycle.setAdapter(popularFoodAdapter);

        RecyclerView recycler_recommended_food = view.findViewById(R.id.recommended_food_list);
        recycler_recommended_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        recycler_recommended_food.setNestedScrollingEnabled(false);
        recycler_recommended_food.setHasFixedSize(false);

        recommendedFoodAdapter = new RecommendedFoodAdapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(FoodItemModel model, int position) {
                if (model.isInWishlist()) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                recommendedFoodAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFoodItemClick(FoodItemModel model) {
                FoodDetailBottomSheet bottomSheet = FoodDetailBottomSheet.newInstance(model);
                bottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "FoodDetailBottomSheet");
            }

        });
        recycler_recommended_food.setAdapter(recommendedFoodAdapter);

        fetchCategories();
        FetchHomeData();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchCategories();
    }

    private void fetchCategories(){
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
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FetchHomeData() {
        if (!NetworkUtils.isInternetAvailable(getContext())) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new NoInternetScreen())
                        .addToBackStack(null) 
                        .commit();
            }
            return;
        }
        //Fetch Category from Firebase


        //Fetch Popular Foods from Firebase
        foodRepository.fetchPopularFoods(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<FoodItemModel> popularFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) popularFoods.add(food);
                }
                popularFoodAdapter.updateData(popularFoods);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Fetch Recommended Foods from Firebase
        foodRepository.fetchRecommendedFoods(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<FoodItemModel> recommendedFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) recommendedFoods.add(food);
                }
                recommendedFoodAdapter.updateData(recommendedFoods);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
