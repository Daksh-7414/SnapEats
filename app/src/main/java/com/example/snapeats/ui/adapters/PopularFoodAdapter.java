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
import com.example.snapeats.data.models.FoodItemModel;

import java.util.ArrayList;

public class PopularFoodAdapter extends RecyclerView.Adapter<PopularFoodAdapter.ViewHolder>{

    Context context;
    ArrayList<FoodItemModel> popular_food_list;
    OnFoodItemActionListener listener;
    public boolean showAll = false;

    public PopularFoodAdapter(Context context, OnFoodItemActionListener listener) {
        this.context = context;
        this.listener = listener;
        popular_food_list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_food_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PopularFoodAdapter.ViewHolder holder, int position) {
        FoodItemModel model = popular_food_list.get(position);
        Glide.with(context)
                .load(model.getFood_image())
                .into(holder.food_image);
        holder.food_name.setText(model.food_name);
        String ratingText = "⭐ " + String.valueOf(model.rating) + " (1.3k)";
        holder.rating.setText(ratingText);
        holder.price.setText("₹"+model.price);

        if (model.isInWishlist()) {
            holder.like_btn.setImageResource(R.drawable.favorite);
        } else {
            holder.like_btn.setImageResource(R.drawable.favorite_border);
        }

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onToggleWishlist(model,position);
            }
        });

        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddToCart(model);
            }
        });

        holder.popular_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFoodItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showAll ? popular_food_list.size() : Math.min(popular_food_list.size(), 4);
    }


    public void updateData(ArrayList<FoodItemModel> popularFoods) {
        this.popular_food_list.clear();
        this.popular_food_list.addAll(popularFoods);
        notifyDataSetChanged();
    }

    // Add this one method
    public void toggleShowAll() {
        showAll = !showAll;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView food_image,like_btn;
        TextView food_name,rating,price;
        ImageButton addtocart;
        RelativeLayout popular_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.food_image);
            food_name = itemView.findViewById(R.id.food_name);
            rating = itemView.findViewById(R.id.food_rating);
            price = itemView.findViewById(R.id.food_price);
            like_btn = itemView.findViewById(R.id.like_btn);
            addtocart = itemView.findViewById(R.id.addtocart);
            popular_layout = itemView.findViewById(R.id.popular_layout);
        }
    }
}
