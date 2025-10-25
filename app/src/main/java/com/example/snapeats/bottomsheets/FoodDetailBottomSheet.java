package com.example.snapeats.bottomsheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.models.FoodItemModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;

public class FoodDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_FOOD_ITEM = "food_item";
    private FoodItemModel model;

    public static FoodDetailBottomSheet newInstance(FoodItemModel foodItem) {
        FoodDetailBottomSheet fragment = new FoodDetailBottomSheet();
        Bundle args = new Bundle();

        Gson gson = new Gson();
        args.putString(ARG_FOOD_ITEM, gson.toJson(foodItem));

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_detailed_screen, container, false);

        if (getArguments() != null) {
            String json = getArguments().getString(ARG_FOOD_ITEM);
            model = new Gson().fromJson(json, FoodItemModel.class);
        }

        bindViews(view);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void bindViews(View view) {
        ImageView img = view.findViewById(R.id.food_image);
        TextView foodName = view.findViewById(R.id.food_name);
        TextView restaurantName = view.findViewById(R.id.food_restaurant_name);
        TextView foodDescription = view.findViewById(R.id.food_description);
        TextView foodPriceDown = view.findViewById(R.id.food_price);
        AppCompatButton addToCart = view.findViewById(R.id.order_btn);
        ImageView wishlistButton = view.findViewById(R.id.wishlist_btn);

        Glide.with(requireContext()).load(model.getFood_image()).into(img);
        foodName.setText(model.getFood_name());
        restaurantName.setText(model.getFood_restaurant_name());
        foodDescription.setText(model.getDescription());
        foodPriceDown.setText("₹" + model.getPrice());

        if (model.isInWishlist()) {
            wishlistButton.setImageResource(R.drawable.favorite);
            model.setInWishlist(true);
        } else {
            wishlistButton.setImageResource(R.drawable.favorite_border);
            model.setInWishlist(false);
        }

        if (model.isInCart()) {
            addToCart.setText("Added");
            model.setInCart(true);
        } else {
            addToCart.setText("Add");
            model.setInCart(false);
        }

        //Add to Cart Functionality
        addToCart.setOnClickListener(v -> {
            if (!model.isInCart()){
                CartManager.getInstance().addToCart(model);
                addToCart.setText("Added");
                Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(view.getContext(), "Item remove in Cart", Toast.LENGTH_SHORT).show();
                CartManager.getInstance().cartRemove(model);
                addToCart.setText("Add");
            }
        });

        //Add to Wishlist Functionality
        wishlistButton.setOnClickListener(v -> {
            if (model.isInWishlist()) {
                WishlistManager.getInstance().removeWishlist(model);
                wishlistButton.setImageResource(R.drawable.favorite_border);
            } else {
                WishlistManager.getInstance().addWishlist(model);
                wishlistButton.setImageResource(R.drawable.favorite);
            }
        });
    }
}
