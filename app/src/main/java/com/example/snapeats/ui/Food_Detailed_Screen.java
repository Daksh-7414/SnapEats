package com.example.snapeats.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.snapeats.R;
import com.example.snapeats.models.Food_Item_Model;

public class Food_Detailed_Screen extends AppCompatActivity {

    ImageView img;
    TextView food_name,restaurant_name;
    TextView food_description;
    ImageButton description_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_detailed_screen);

        img = findViewById(R.id.food_image);
        food_name = findViewById(R.id.food_name);
        restaurant_name = findViewById(R.id.food_restaurant_name);

        food_description = findViewById(R.id.food_description);
        description_btn = findViewById(R.id.description_btn);
        description_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (food_description.getVisibility() == View.VISIBLE) {
                    food_description.setVisibility(View.GONE);
                    description_btn.setRotation(270);
                }else {
                    food_description.setVisibility(View.VISIBLE);
                    description_btn.setRotation(90);
                }
            }
        });

        Food_Item_Model foodModel = (Food_Item_Model) getIntent().getSerializableExtra("foodModel");

        try {
            img.setImageResource(foodModel.getFood_image());
            food_name.setText(foodModel.getFood_name());
            restaurant_name.setText(foodModel.getFood_restaurant_name());
        } catch (Exception e) {
            Log.e("error",e.getMessage());
        }

    }
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed(); // Go back on back arrow press
//        return true;
//    }

}