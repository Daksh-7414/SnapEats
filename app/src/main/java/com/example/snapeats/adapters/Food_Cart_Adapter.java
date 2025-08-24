package com.example.snapeats.adapters;



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
import com.example.snapeats.interfaces.OnCartActionListener;
import com.example.snapeats.models.Food_Item_Model;


import java.util.ArrayList;

public class Food_Cart_Adapter extends RecyclerView.Adapter<Food_Cart_Adapter.ViewHolder>{

    Context context;
    ArrayList<Food_Item_Model> cart_food_list;
    OnCartActionListener listener;

    public Food_Cart_Adapter(Context context, ArrayList<Food_Item_Model> cart_food_list, OnCartActionListener listener) {
        this.context = context;
        this.cart_food_list = cart_food_list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_food_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food_Item_Model model = cart_food_list.get(position);
        Glide.with(context)
                .load(model.getFood_image())
                .into(holder.food_image);
        holder.food_name.setText(model.food_name);
        holder.food_restaurant.setText(model.food_restaurant_name);
        holder.price.setText(model.price);
        holder.cart_count.setText(String.valueOf(model.cart_count));

        holder.price.setText("₹" + (Integer.parseInt(model.price.replace("₹","").trim()) * model.cart_count));

        holder.cart_add.setOnClickListener(v ->{
            listener.onCartIncrement(model);
        });

        holder.cart_minus.setOnClickListener(v -> {
            if (model.cart_count > 1) {
                listener.onCartDecrement(model);
            }else {
                listener.onCartRemove(model, holder.getAdapterPosition());
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView food_image;
        TextView food_name,food_restaurant,price,cart_count;
        ImageButton cart_minus,cart_add;
        ConstraintLayout cart_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.cart_food_image);
            food_name = itemView.findViewById(R.id.cart_food_name);
            food_restaurant = itemView.findViewById(R.id.food_restaurant_name);
            price = itemView.findViewById(R.id.cart_food_price);
            cart_count = itemView.findViewById(R.id.cart_count);
            cart_minus = itemView.findViewById(R.id.cart_minus);
            cart_add = itemView.findViewById(R.id.cart_add);
            cart_layout = itemView.findViewById(R.id.cart_layout);
        }
    }
}
