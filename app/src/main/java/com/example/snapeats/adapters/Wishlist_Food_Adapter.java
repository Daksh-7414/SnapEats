package com.example.snapeats.adapters;

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

public class Wishlist_Food_Adapter extends RecyclerView.Adapter<Wishlist_Food_Adapter.ViewHolder> {
    Context context;
    ArrayList<Food_Item_Model> wishlist_food_item;
    OnFoodItemActionListener listener;


    public Wishlist_Food_Adapter(Context context, ArrayList<Food_Item_Model> wishlist_food_item, OnFoodItemActionListener listener) {
        this.context = context;
        this.wishlist_food_item = wishlist_food_item;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommended_food_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food_Item_Model model = wishlist_food_item.get(position);
        Glide.with(context)
                .load(model.getFood_image())
                .into(holder.food_image);
        holder.food_name.setText(model.food_name);
        holder.food_restaurant.setText(model.food_restaurant_name);
        holder.price.setText(model.price);
        holder.like_btn.setImageResource(R.drawable.favorite);
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

        holder.like_btn.setOnClickListener(v -> listener.onToggleWishlist(model,position));

        holder.addtocart.setOnClickListener(v -> listener.onAddToCart(model));

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


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView food_image,like_btn;
        TextView food_name,food_restaurant,price;
        ImageButton addtocart;
        ConstraintLayout wishlist_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.recommended_food_image);
            food_name = itemView.findViewById(R.id.recommended_food_name);
            food_restaurant = itemView.findViewById(R.id.food_restaurant_name);
            price = itemView.findViewById(R.id.recommended_food_price);
            like_btn = itemView.findViewById(R.id.like_btn);
            addtocart = itemView.findViewById(R.id.addtocart);
            wishlist_layout = itemView.findViewById(R.id.recommended_layout);
        }
    }
}
