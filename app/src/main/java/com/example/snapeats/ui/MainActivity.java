package com.example.snapeats.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.snapeats.R;
import com.example.snapeats.fragements.cart_screen;
import com.example.snapeats.fragements.home_screen;
import com.example.snapeats.fragements.profile_screen;
import com.example.snapeats.fragements.wishlist_screen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

//    ArrayList<Categories_model> categoryList = new ArrayList<>();
//    ArrayList<Popular_food_model> popularFood_List = new ArrayList<>();
//    ArrayList<Recommended_model> recommended_food_list = new ArrayList<>();
//
//    CategoryAdapter categoryAdapter;
//    Popular_food_Adapter popularFoodAdapter;
//    Recommended_Food_Adapter recommendedFoodAdapter;
    private int selectedItemId = R.id.nav_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bnView = findViewById(R.id.bnView);

        bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                selectedItemId = id;
                if (id == R.id.nav_home){
                    loadFrag(new home_screen(), false);
                } else if (id == R.id.nav_wishlist) {
                    loadFrag(new wishlist_screen(), false);
                } else if (id == R.id.nav_cart) {
                    loadFrag(new cart_screen(), false);
                } else {
                    loadFrag(new profile_screen(), true);
                }
                return true;
            }
        });

        bnView.setSelectedItemId(R.id.nav_home);

    }
    public void loadFrag(Fragment fragment, boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag)
            ft.add(R.id.container, fragment);
        else
            ft.replace(R.id.container,fragment);

        ft.commit();
    }
    @Override
    public void onBackPressed() {
        if (selectedItemId != R.id.nav_home) {
            selectedItemId = R.id.nav_home;
            BottomNavigationView bnView = findViewById(R.id.bnView);
            bnView.setSelectedItemId(R.id.nav_home); // This will auto-load Home
        } else {
            super.onBackPressed(); // Actually exit the app
        }
    }
}