package com.example.snapeats.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.data.models.FoodItemModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrderedFoodAdapter
        extends RecyclerView.Adapter<OrderedFoodAdapter.OrderedFoodViewHolder> {

    private Context context;
    private ArrayList<FoodItemModel> orderedFoodList;

    public OrderedFoodAdapter(Context context) {
        this.context = context;
        orderedFoodList = new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderedFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.order_food_card, parent, false);
        return new OrderedFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedFoodViewHolder holder, int position) {

        FoodItemModel model = orderedFoodList.get(position);

        holder.foodName.setText(model.getFood_name());
        holder.restaurantName.setText(model.getFood_restaurant_name());
        //holder.rating.setText("⭐ " + model.getRating());
        String ratingText = "⭐ " + String.valueOf(model.rating) + " (1.3k)";
        holder.rating.setText(ratingText);
        holder.priceQty.setText("₹"+ (model.getPrice()*model.getCart_count())+" : Qty: " + model.getCart_count());
        //holder.qty.setText(" : Qty: " + model.getCart_count());
        //holder.deliveryDate.setText("• Delivered on " + model.getDeliveryDate());

        // Image loading (Glide recommended)

        Glide.with(context)
                .load(model.getFood_image())
                .placeholder(R.drawable.no_image) // loading ke time
                .error(R.drawable.no_image)
                .into(holder.foodImage);


//        holder.trackOrder.setOnClickListener(v -> {
//            // Handle Track Order click
//        });
    }

    public void updateData(ArrayList<FoodItemModel> cartfoods) {
        this.orderedFoodList.clear();
        this.orderedFoodList.addAll(cartfoods);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderedFoodList.size();
    }

    static class OrderedFoodViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodName, restaurantName, rating, deliveryDate;
        AppCompatButton trackOrder;
        AppCompatTextView priceQty;

        public OrderedFoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImage = itemView.findViewById(R.id.recommended_food_image);
            foodName = itemView.findViewById(R.id.order_food_name);
            restaurantName = itemView.findViewById(R.id.food_restaurant_name);
            rating = itemView.findViewById(R.id.food_rating);
            deliveryDate = itemView.findViewById(R.id.textView15);
            priceQty = itemView.findViewById(R.id.order_price);
            trackOrder = itemView.findViewById(R.id.addtocart);
        }
    }
}
