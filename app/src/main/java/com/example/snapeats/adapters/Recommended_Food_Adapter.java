package com.example.snapeats.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.Food_Item_Model;

import java.util.ArrayList;

public class Recommended_Food_Adapter extends RecyclerView.Adapter<Recommended_Food_Adapter.ViewHolder> {

    Context context;
    ArrayList<Food_Item_Model> recommended_food_list;
    OnFoodItemActionListener listener;

    public Recommended_Food_Adapter(Context context, OnFoodItemActionListener listener) {
        this.context = context;
        recommended_food_list = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public Recommended_Food_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommended_food_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Recommended_Food_Adapter.ViewHolder holder, int position) {
        Food_Item_Model model = recommended_food_list.get(position);
        Glide.with(context)
                .load(model.getFood_image())
                .into(holder.food_image);
        holder.food_name.setText(model.food_name);
        holder.food_restaurant.setText(model.food_restaurant_name);
        holder.price.setText("₹"+model.price);

        holder.food_name.post(() -> {
            int lineCount = holder.food_name.getLineCount();

            if (lineCount > 1) {
                holder.food_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
                holder.food_name.requestLayout();
                holder.food_name.invalidate();
            } else {
                holder.food_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                holder.food_name.requestLayout();
                holder.food_name.invalidate();
            }
        });


        if (model.isInWishlist()) {
            holder.like_btn.setImageResource(R.drawable.favorite);
            model.setInWishlist(true);
        } else {
            holder.like_btn.setImageResource(R.drawable.favorite_border);
            model.setInWishlist(false);
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
        holder.recommended_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFoodItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommended_food_list.size();
    }

    public void updateData(ArrayList<Food_Item_Model> recommendedFoods) {
        this.recommended_food_list.clear();
        this.recommended_food_list.addAll(recommendedFoods);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView food_image,like_btn;
        TextView food_name,food_restaurant,price;
        ImageButton addtocart;
        ConstraintLayout recommended_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.recommended_food_image);
            food_name = itemView.findViewById(R.id.recommended_food_name);
            food_restaurant = itemView.findViewById(R.id.food_restaurant_name);
            price = itemView.findViewById(R.id.recommended_food_price);
            like_btn = itemView.findViewById(R.id.like_btn);
            addtocart = itemView.findViewById(R.id.addtocart);
            recommended_layout = itemView.findViewById(R.id.recommended_layout);
        }
    }
}
