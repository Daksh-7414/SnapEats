package com.example.snapeats.fragements;

import static com.example.snapeats.adapters.Food_Cart_Adapter.cart_food_list;
import static com.example.snapeats.adapters.Wishlist_Food_Adapter.wishlist_food_item;
import static com.example.snapeats.fragements.cart_screen.gotocart;
import static com.example.snapeats.fragements.wishlist_screen.gotowishlist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<Categories_model> categoryList = new ArrayList<>();
    ArrayList<Food_Item_Model> popularFood_List = new ArrayList<>();
    ArrayList<Food_Item_Model> recommended_food_list = new ArrayList<>();

    CategoryAdapter categoryAdapter;
    Popular_food_Adapter popularFoodAdapter;
    Recommended_Food_Adapter recommendedFoodAdapter;


    public HomeScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeScreen newInstance(String param1, String param2) {
        HomeScreen fragment = new HomeScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        categoryList.add(new Categories_model(R.drawable.donut,"Donut"));
        categoryList.add(new Categories_model(R.drawable.cola,"Drinks"));
        categoryList.add(new Categories_model(R.drawable.hotdog,"Hot Dog"));
        categoryList.add(new Categories_model(R.drawable.sandwich,"Sandwich"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(categoryAdapter);

        TextView viewCategory = view.findViewById(R.id.viewCategory);
        viewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewCat = new Intent(getContext(), ViewCategory.class);
                startActivity(viewCat); // <-- You missed this line
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

        //load Popular food items
        popularFood_List.add(new Food_Item_Model(R.drawable.burger_img,"Chicken Burger","Cookie Heaven","₹30"));
        popularFood_List.add(new Food_Item_Model(R.drawable.burger_img,"Cheese Pizza","Cookie Heaven","₹30"));
        popularFood_List.add(new Food_Item_Model(R.drawable.burger_img,"Sugar Ring Donut","Cookie Heaven","₹30"));
        popularFood_List.add(new Food_Item_Model(R.drawable.burger_img,"Spicy Hot Dog","Cookie Heaven","₹30"));
        popularFood_List.add(new Food_Item_Model(R.drawable.burger_img,"Crunch Bite Sandwich","Cookie Heaven","₹30"));
        popularFood_List.add(new Food_Item_Model(R.drawable.burger_img,"Margherita Pizza","Cookie Heaven","₹30"));

        popularFoodAdapter = new Popular_food_Adapter(getContext(), popularFood_List, new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!cart_food_list.contains(model)){
                    model.cart_count++;
                    gotocart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (wishlist_food_item.contains(model)) {
                    wishlist_food_item.remove(model);
                } else {
                    gotowishlist(model);
                }
                popularFoodAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFoodItemClick(Food_Item_Model model) {
                Intent intent = new Intent(getContext(), Food_Detailed_Screen.class);
                intent.putExtra("foodModel", model);
                startActivity(intent);
            }
        });
        recycler_popular_food.setAdapter(popularFoodAdapter);

        RecyclerView recycler_recommended_food = view.findViewById(R.id.recommended_food_list);
        recycler_recommended_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        recycler_recommended_food.setNestedScrollingEnabled(false);
        recycler_recommended_food.setHasFixedSize(false);

        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Chicken Burger","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Cheese Pizza","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Sugar Ring Donut","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Spicy Hot Dog","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Crunch Bite Sandwich","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Margherita Pizza","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Crispy Veg Burger","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Aloo Cheese Sandwich","Cookie Heaven","₹30"));
        recommended_food_list.add(new Food_Item_Model(R.drawable.burger_img,"Chicken Burger","Cookie Heaven","₹30"));

        recommendedFoodAdapter = new Recommended_Food_Adapter(getContext(), recommended_food_list, new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!cart_food_list.contains(model)){
                    model.cart_count++;
                    gotocart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (wishlist_food_item.contains(model)) {
                    wishlist_food_item.remove(model);
                } else {
                    gotowishlist(model);
                }
                recommendedFoodAdapter.notifyItemChanged(position);

            }

            @Override
            public void onFoodItemClick(Food_Item_Model model) {
                Intent intent = new Intent(getContext(), Food_Detailed_Screen.class);
                intent.putExtra("foodModel", model);
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
}