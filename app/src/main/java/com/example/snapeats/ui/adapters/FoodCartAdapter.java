package com.example.snapeats.ui.adapters;



import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.snapeats.data.interfaces.OnCartActionListener;
import com.example.snapeats.data.models.FoodItemModel;


import java.util.ArrayList;

public class FoodCartAdapter extends RecyclerView.Adapter<FoodCartAdapter.ViewHolder>{

    Context context;
    ArrayList<FoodItemModel> cart_food_list;
    OnCartActionListener listener;

    public FoodCartAdapter(Context context, OnCartActionListener listener) {
        this.context = context;
        cart_food_list = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_food_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItemModel model = cart_food_list.get(position);
        Glide.with(context)
                .load(model.getFood_image())
                .placeholder(R.drawable.no_image) // loading ke time
                .error(R.drawable.no_image)
                .into(holder.food_image);
        holder.food_name.setText(model.food_name);
        holder.food_restaurant.setText(model.food_restaurant_name);
        holder.price.setText("₹"+model.price);
        holder.cart_count.setText(String.valueOf(model.cart_count));
        String ratingText = "⭐ " + String.valueOf(model.rating) + " (1.3k)";
        holder.rating.setText(ratingText);
//        holder.price.setText("₹" + (Integer.parseInt(model.price.replace("₹","").trim()) * model.cart_count));
        holder.price.setText("₹" + (model.price * model.cart_count));
        holder.cart_add.setOnClickListener(v ->{
            listener.onCartIncrement(model);
        });

        holder.cart_minus.setOnClickListener(v -> {
            if (model.cart_count > 1) {
                listener.onCartDecrement(model);
            }else {
                listener.onCartRemove(model);
            }
        });

        holder.cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // listener.onFoodItemClick(model);
            }
        });

    }


    @Override
    public int getItemCount() {
        return cart_food_list.size();
    }

    public void updateData(ArrayList<FoodItemModel> cartfoods) {
        this.cart_food_list.clear();
        this.cart_food_list.addAll(cartfoods);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView food_image;
        TextView food_name,food_restaurant,price,cart_count,rating;
        ImageButton cart_minus,cart_add;
        ConstraintLayout cart_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.cart_food_image);
            food_name = itemView.findViewById(R.id.cart_food_name);
            food_restaurant = itemView.findViewById(R.id.food_restaurant_name);
            price = itemView.findViewById(R.id.cart_food_price);
            cart_count = itemView.findViewById(R.id.cart_count);
            rating = itemView.findViewById(R.id.textView3);
            cart_minus = itemView.findViewById(R.id.cart_minus);
            cart_add = itemView.findViewById(R.id.cart_add);
            cart_layout = itemView.findViewById(R.id.cart_layout);
        }
    }
}
