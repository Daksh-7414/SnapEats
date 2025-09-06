package com.example.snapeats.fragements;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.models.CategoriesModel;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.ui.Food_Detailed_Screen;
import com.example.snapeats.R;
import com.example.snapeats.ui.ViewCategory;
import com.example.snapeats.ui.ViewPopularFood;
import com.example.snapeats.adapters.CategoryAdapter;
import com.example.snapeats.adapters.Popular_food_Adapter;
import com.example.snapeats.adapters.Recommended_Food_Adapter;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.Food_Item_Model;
import com.example.snapeats.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class home_screen extends Fragment {

    private CategoryAdapter categoryAdapter;
    private Popular_food_Adapter popularFoodAdapter;
    private Recommended_Food_Adapter recommendedFoodAdapter;

    private FoodRepository foodRepository;

    ArrayList<Food_Item_Model> allfoodlist;

    public home_screen() {
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

        // Load Categories
//        categoryList.add(new Categories_model(R.drawable.pizza,"Pizza"));
//        categoryList.add(new Categories_model(R.drawable.burger,"Burger"));
//        categoryList.add(new Categories_model(R.drawable.penne,"Pasta"));
//        categoryList.add(new Categories_model(R.drawable.cola,"Drinks"));
//        categoryList.add(new Categories_model(R.drawable.cake,"Dessert"));
//        categoryList.add(new Categories_model(R.drawable.sandwich,"Sandwich"));

        categoryAdapter = new CategoryAdapter(getContext());
        recyclerView.setAdapter(categoryAdapter);

        TextView viewCategory = view.findViewById(R.id.viewCategory);
        viewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewCat = new Intent(getContext(), ViewCategory.class);
                startActivity(viewCat);
            }
        });

        TextView viewPopular = view.findViewById(R.id.viewPopularfood);
        viewPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewPop = new Intent(getContext(), ViewPopularFood.class);
                startActivity(viewPop); // <-- You missed this line
            }
        });


        RecyclerView popularFoodRecycle = view.findViewById(R.id.popular_food_list);
        popularFoodRecycle.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        popularFoodAdapter = new Popular_food_Adapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (model.isInWishlist()) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                popularFoodAdapter.notifyItemChanged(position, "wishlist");
            }

            @Override
            public void onFoodItemClick(Food_Item_Model model) {
                Gson gson = new Gson();
                String json = gson.toJson(model);
                Intent intent = new Intent(getContext(), Food_Detailed_Screen.class);
                intent.putExtra("foodModel", json);
                startActivity(intent);
            }
        });
        popularFoodRecycle.setAdapter(popularFoodAdapter);

        RecyclerView recycler_recommended_food = view.findViewById(R.id.recommended_food_list);
        recycler_recommended_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        recycler_recommended_food.setNestedScrollingEnabled(false);
        recycler_recommended_food.setHasFixedSize(false);

        recommendedFoodAdapter = new Recommended_Food_Adapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (model.isInWishlist()) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                recommendedFoodAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFoodItemClick(Food_Item_Model model) {
                Gson gson = new Gson();
                String json = gson.toJson(model);
                Intent intent = new Intent(getContext(), Food_Detailed_Screen.class);
                intent.putExtra("foodModel", json);
                startActivity(intent);
            }

        });
        recycler_recommended_food.setAdapter(recommendedFoodAdapter);

        FetchHomeData();

        return view;
    }

    private void FetchHomeData() {
        if (!NetworkUtils.isInternetAvailable(getContext())) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new NoInternetScreen())
                        .addToBackStack(null)  // taaki retry pe back stack se pichla fragment aaye
                        .commit();
            }
            return;
        }
        //Fetch Category from Firebase
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

        //Fetch Popular Foods from Firebase
        foodRepository.fetchPopularFoods(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Food_Item_Model> popularFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Food_Item_Model food = child.getValue(Food_Item_Model.class);
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
                ArrayList<Food_Item_Model> recommendedFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Food_Item_Model food = child.getValue(Food_Item_Model.class);
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
