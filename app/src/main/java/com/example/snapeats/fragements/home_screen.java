package com.example.snapeats.fragements;


import static com.example.snapeats.fragements.cart_screen.gotocart;
import static com.example.snapeats.fragements.wishlist_screen.gotowishlist;
import static com.example.snapeats.repository.FoodRepository.cartFoods;
import static com.example.snapeats.repository.FoodRepository.recommendedFoods;
import static com.example.snapeats.repository.FoodRepository.wishlistFoods;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.ui.Food_Detailed_Screen;
import com.example.snapeats.R;
import com.example.snapeats.ui.ViewCategory;
import com.example.snapeats.ui.ViewPopularFood;
import com.example.snapeats.adapters.CategoryAdapter;
import com.example.snapeats.adapters.Popular_food_Adapter;
import com.example.snapeats.adapters.Recommended_Food_Adapter;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.Categories_model;
import com.example.snapeats.models.Food_Item_Model;
import com.google.gson.Gson;

import java.util.ArrayList;

public class home_screen extends Fragment {

    ArrayList<Categories_model> categoryList = new ArrayList<>();
    ArrayList<Food_Item_Model> popularList = new ArrayList<>();
//    public static ArrayList<Food_Item_Model> popularFood_List = new ArrayList<>();
//    public static ArrayList<Food_Item_Model> recommended_food_list = new ArrayList<>();
//    public static ArrayList<Food_Item_Model> wishlist_food_item = new ArrayList<>();
//    public static ArrayList<Food_Item_Model> cart_food_list = new ArrayList<>();

    CategoryAdapter categoryAdapter;
    Popular_food_Adapter popularFoodAdapter;
    Recommended_Food_Adapter recommendedFoodAdapter;


    ArrayList<Food_Item_Model> allfoodlist;

//    public home_screen() {
//        // Required empty public constructor
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        categoryList.add(new Categories_model(R.drawable.pizza,"Pizza"));
        categoryList.add(new Categories_model(R.drawable.burger,"Burger"));
        categoryList.add(new Categories_model(R.drawable.penne,"Pasta"));
        categoryList.add(new Categories_model(R.drawable.cola,"Drinks"));
        categoryList.add(new Categories_model(R.drawable.cake,"Dessert"));
        categoryList.add(new Categories_model(R.drawable.sandwich,"Sandwich"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
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


        RecyclerView recycler_popular_food = view.findViewById(R.id.popular_food_list);
        recycler_popular_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        popularList = FoodRepository.getPopularFoods();
        Log.d("popular food list",popularList.size()+"");
        popularFoodAdapter = new Popular_food_Adapter(getContext(), FoodRepository.getPopularFoods(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (model.isInCart()){
                    model.cart_count++;
                    gotocart(model);
                    model.setInCart(true);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (model.isInWishlist()) {
                    model.setInWishlist(false);
                    wishlistFoods.remove(model);
                } else {
                    gotowishlist(model);
                }
                popularFoodAdapter.notifyItemChanged(position);
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
        recycler_popular_food.setAdapter(popularFoodAdapter);

        RecyclerView recycler_recommended_food = view.findViewById(R.id.recommended_food_list);
        recycler_recommended_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        recycler_recommended_food.setNestedScrollingEnabled(false);
        recycler_recommended_food.setHasFixedSize(false);

        recommendedFoodAdapter = new Recommended_Food_Adapter(getContext(), recommendedFoods, new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!cartFoods.contains(model)){
                    model.cart_count++;
                    gotocart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (model.isInWishlist()) {
                    model.setInWishlist(false);
                    wishlistFoods.remove(model);
                } else {
                    gotowishlist(model);
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

        return view;
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (recommendedFoodAdapter != null) {
            recommendedFoodAdapter.notifyDataSetChanged();
        }
        if (popularFoodAdapter != null) {
            popularFoodAdapter.notifyDataSetChanged();
        }
    }

    public void refreshData() {
        popularFoodAdapter.updateData(FoodRepository.getPopularFoods());
        Log.d("popular refresh",FoodRepository.getPopularFoods().size()+"");
    }
}
