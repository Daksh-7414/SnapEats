package com.example.snapeats.ui.adapters;

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

public class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.ViewHolder>{

    Context context;
    ArrayList<FoodItemModel> bottomlist;
    OnFoodItemActionListener listener;

    public BottomAdapter(Context context, OnFoodItemActionListener listener) {
        this.context = context;
        this.listener = listener;
        bottomlist = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_food_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull BottomAdapter.ViewHolder holder, int position) {
        FoodItemModel model = bottomlist.get(position);
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
        return bottomlist.size();
    }

    public void updateData(ArrayList<FoodItemModel> popularFoods) {
        this.bottomlist.clear();
        this.bottomlist.addAll(popularFoods);
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
