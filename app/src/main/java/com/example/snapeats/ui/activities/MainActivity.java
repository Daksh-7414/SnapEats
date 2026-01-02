package com.example.snapeats.ui.activities;



import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.snapeats.R;
import com.example.snapeats.ui.fragements.NoInternetScreen;
import com.example.snapeats.ui.fragements.CartScreenFragment;
import com.example.snapeats.ui.fragements.HomeScreenFragment;
import com.example.snapeats.ui.fragements.ProfileScreenFragment;
import com.example.snapeats.ui.fragements.WishlistScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private int selectedItemId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        // Setup navigation first (so UI is ready even if data loads later)
        BottomNavigationView bnView = findViewById(R.id.bnView);
        if (bnView == null) {
            Log.e("MainActivity", "BottomNavigationView not found - check XML");
            return;
        }


        bnView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            selectedItemId = id;
            if (id == R.id.nav_home) {
                loadFrag(new HomeScreenFragment(), false,"HOME");
            } else if (id == R.id.nav_wishlist) {
                loadFrag(new WishlistScreenFragment(), false,"LIKE");
            } else if (id == R.id.nav_cart) {
                loadFrag(new CartScreenFragment(), false,"CART");
            } else {
                loadFrag(new ProfileScreenFragment(), true,"PROFILE");
            }
            return true;
        });
        bnView.setSelectedItemId(R.id.nav_home);
    }

    public void loadFrag(Fragment fragment, boolean flag, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag) {
            ft.add(R.id.container, fragment,tag);
        } else {
            ft.replace(R.id.container, fragment,tag);
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (selectedItemId != R.id.nav_home) {
            selectedItemId = R.id.nav_home;
            BottomNavigationView bnView = findViewById(R.id.bnView);
            bnView.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }

    private void showNoInternetScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new NoInternetScreen())
                .commit();
    }
}
