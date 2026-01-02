package com.example.snapeats.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.data.interfaces.OnFoodItemActionListener;
import com.example.snapeats.data.managers.CartManager;
import com.example.snapeats.data.models.FoodItemModel;

import java.util.ArrayList;

public class WishlistFoodAdapter extends RecyclerView.Adapter<WishlistFoodAdapter.ViewHolder> {
    Context context;
    ArrayList<FoodItemModel> wishlist_food_item;
    OnFoodItemActionListener listener;


    public WishlistFoodAdapter(Context context, OnFoodItemActionListener listener) {
        this.context = context;
        wishlist_food_item = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.horizontal_food_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItemModel model = wishlist_food_item.get(position);
        Glide.with(context)
                .load(model.getFood_image())
                .placeholder(R.drawable.no_image) // loading ke time
                .error(R.drawable.no_image)
                .into(holder.food_image);
        holder.food_name.setText(model.food_name);
        holder.food_restaurant.setText(model.food_restaurant_name);
        String ratingText = "⭐ " + String.valueOf(model.rating) + " (1.3k)";
        holder.rating.setText(ratingText);
        holder.price.setText("₹"+model.price);
        holder.like_btn.setImageResource(R.drawable.favorite);

        holder.like_btn.setOnClickListener(v -> listener.onToggleWishlist(model,position));

        if (CartManager.getInstance().isInCart(model.getId())){
            holder.addtocart.setImageResource(R.drawable.already_cart);
        }else{
            holder.addtocart.setImageResource(R.drawable.add_circle);
            holder.addtocart.setClickable(true);
            holder.addtocart.setEnabled(true);
        }

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddToCart(model);
                holder.addtocart.setImageResource(R.drawable.already_cart);
                holder.addtocart.setClickable(false);
                holder.addtocart.setEnabled(false);
            }
        });

        holder.wishlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFoodItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlist_food_item.size();
    }

    public void updateData(ArrayList<FoodItemModel> wishlistfoods) {
        this.wishlist_food_item.clear();
        this.wishlist_food_item.addAll(wishlistfoods);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView food_image,like_btn;
        TextView food_name,food_restaurant,price,rating;
        ImageButton addtocart;
        RelativeLayout wishlist_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.recommended_food_image);
            food_name = itemView.findViewById(R.id.recommended_food_name);
            food_restaurant = itemView.findViewById(R.id.food_restaurant_name);
            price = itemView.findViewById(R.id.recommended_food_price);
            rating = itemView.findViewById(R.id.textView3);
            like_btn = itemView.findViewById(R.id.like_btn);
            addtocart = itemView.findViewById(R.id.addtocart);
            wishlist_layout = itemView.findViewById(R.id.recommended_layout);
        }
    }
}
