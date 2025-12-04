package com.example.snapeats.ui.detail;

//import static com.example.snapeats.fragements.cart_screen.gotocart;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.snapeats.R;
import com.example.snapeats.data.models.FoodItemModel;
import com.google.gson.Gson;

public class FoodDetailScreen extends AppCompatActivity {

    ImageView img;
    TextView food_name,restaurant_name;
    TextView food_description,food_price_top,food_price_down;
    ImageButton description_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_detailed_screen);

        img = findViewById(R.id.food_image);
//        food_name = findViewById(R.id.food_name);
//        restaurant_name = findViewById(R.id.food_restaurant_name);
//        food_description = findViewById(R.id.food_description);
//        food_price_top = findViewById(R.id.food_price_top);
//        food_price_down = findViewById(R.id.food_price);
//
//
//        description_btn = findViewById(R.id.description_btn);
//        description_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (food_description.getVisibility() == View.VISIBLE) {
//                    food_description.setVisibility(View.GONE);
//                    description_btn.setRotation(270);
//                }else {
//                    food_description.setVisibility(View.VISIBLE);
//                    description_btn.setRotation(90);
//                }
//            }
//        });

        String json = getIntent().getStringExtra("foodModel");
        Gson gson = new Gson();
        FoodItemModel model = gson.fromJson(json, FoodItemModel.class);

        try {
            Glide.with(this)
                    .load(model.getFood_image())
                    .into(img);
            food_name.setText(model.getFood_name());
            restaurant_name.setText(model.getFood_restaurant_name());
            food_description.setText(model.getDescription());
            food_price_top.setText(model.price);
            food_price_down.setText(model.price);

        } catch (Exception e) {
            Log.e("error",e.getMessage());
        }
//        AppCompatButton order_btn = findViewById(R.id.order_btn);
//        order_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (model.isInCart()){
//                    model.cart_count++;
//                    //gotocart(model);
//                    model.setInCart(true);
//                    Toast.makeText(FoodDetailScreen.this, "Item Add to Cart", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(FoodDetailScreen.this, "Item Already in Cart", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Go back on back arrow press
//        return true;
//    }

}