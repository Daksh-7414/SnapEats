package com.example.snapeats.ui;

import static com.example.snapeats.repository.FoodRepository.fetchFoods;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.snapeats.R;
import com.example.snapeats.firebase.FireBaseConnection;
import com.example.snapeats.fragements.NoInternetScreen;
import com.example.snapeats.fragements.cart_screen;
import com.example.snapeats.fragements.home_screen;
import com.example.snapeats.fragements.profile_screen;
import com.example.snapeats.fragements.wishlist_screen;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.utils.NetworkUtils;
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

        // STEP 1: Check Internet
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "Net connection failed ❌", Toast.LENGTH_SHORT).show();
            showNoInternetScreen();
            return;
        }

        // STEP 2: Check Firebase Connection
        FireBaseConnection.checkFirebaseConnection(new FireBaseConnection.FirebaseConnectionListener() {
            @Override
            public void onConnected() {
                Log.d("MainActivity", "✅ Firebase connected");
                Toast.makeText(MainActivity.this, "Firebase connected successfully", Toast.LENGTH_SHORT).show();
                // Proceed to fetch data (only once)
                FoodRepository.fetchFoods(() -> {
                    Log.d("MainActivity", "✅ Foods fetched. Size: " + FoodRepository.getAllFoods().size());
                    runOnUiThread(() -> {
                        home_screen fragment = (home_screen) getSupportFragmentManager()
                                .findFragmentByTag("HOME");
                        if (fragment != null) {
                            fragment.refreshData(); // method inside fragment to reload adapter
                        }
                    });
                });
            }

            @Override
            public void onDisconnected() {
                Log.w("MainActivity", "❌ Firebase disconnected");
                Toast.makeText(MainActivity.this, "Firebase connection lost - trying to reconnect", Toast.LENGTH_SHORT).show();
            }
        });

        bnView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            selectedItemId = id;
            if (id == R.id.nav_home) {
                loadFrag(new home_screen(), false,"HOME");
            } else if (id == R.id.nav_wishlist) {
                loadFrag(new wishlist_screen(), false,"LIKE");
            } else if (id == R.id.nav_cart) {
                loadFrag(new cart_screen(), false,"CART");
            } else {
                loadFrag(new profile_screen(), true,"PROFILE");
            }
            return true;
        });
        bnView.setSelectedItemId(R.id.nav_home);
    }

    private void refreshCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
        if (current == null) {
            Log.w("MainActivity", "No fragment loaded to refresh");
            return;
        }
        if (current instanceof home_screen) {
            ((home_screen) current).refreshData(); // Calls your fragment's refresh method
        } // Add similar for other data-dependent fragments if needed
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

    //
    private void showNoInternetScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new NoInternetScreen())
                .commit();
    }
}
