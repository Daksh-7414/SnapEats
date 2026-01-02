package com.example.snapeats.ui.bottomsheets;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.data.interfaces.OnFoodUpdatedListener;
import com.example.snapeats.data.managers.CartManager;
import com.example.snapeats.data.managers.WishlistManager;
import com.example.snapeats.data.models.FoodItemModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FoodDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_FOOD_ITEM = "food_item";
    private FoodItemModel model;
    private OnFoodUpdatedListener listener;

    public static FoodDetailBottomSheet newInstance(FoodItemModel foodItem) {
        FoodDetailBottomSheet fragment = new FoodDetailBottomSheet();
        fragment.model = foodItem;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_detailed_screen, container, false);

        bindViews(view);

        return view;
    }

    public void setOnFoodUpdatedListener(OnFoodUpdatedListener listener){
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(listener != null){
            listener.onFoodUpdated();
        }
    }

    @SuppressLint("SetTextI18n")
    private void bindViews(View view) {
        ImageView img = view.findViewById(R.id.food_image);
        TextView foodName = view.findViewById(R.id.food_name);
        TextView restaurantName = view.findViewById(R.id.food_restaurant_name);
        TextView foodDescription = view.findViewById(R.id.food_description);
        TextView foodPriceDown = view.findViewById(R.id.food_price);
        TextView rating = view.findViewById(R.id.food_rating);
        AppCompatButton addToCart = view.findViewById(R.id.order_btn);
        ImageView wishlistButton = view.findViewById(R.id.wishlist_btn);

        Glide.with(requireContext()).load(model.getFood_image()).into(img);
        foodName.setText(model.getFood_name());
        restaurantName.setText(model.getFood_restaurant_name());
        foodDescription.setText(model.getDescription());
        foodPriceDown.setText("₹" + model.getPrice());
        String ratingText = "⭐ " + String.valueOf(model.rating) + " (1.3k)";
        rating.setText(ratingText);

        if (WishlistManager.getInstance().isInWishlist(model.getId())) {
            wishlistButton.setImageResource(R.drawable.favorite);
        } else {
            wishlistButton.setImageResource(R.drawable.favorite_border);
        }

        if (CartManager.getInstance().isInCart(model.getId())) {
            addToCart.setText("Added");
        } else {
            addToCart.setText("Add");
        }

        //Add to Cart Functionality
        addToCart.setOnClickListener(v -> {
            if (!CartManager.getInstance().isInCart(model.getId())) {
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
            if (WishlistManager.getInstance().isInWishlist(model.getId())) {
                WishlistManager.getInstance().removeWishlist(model);
                wishlistButton.setImageResource(R.drawable.favorite_border);
            } else {
                WishlistManager.getInstance().addWishlist(model);
                wishlistButton.setImageResource(R.drawable.favorite);
            }

        });
    }
}
