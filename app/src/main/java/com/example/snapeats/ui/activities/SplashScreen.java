package com.example.snapeats.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snapeats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapeats_splash);

        new Handler().postDelayed(this::checkUserAuthentication, 1000);
    }

    private void checkUserAuthentication() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userEmail = sharedPreferences.getString("userEmail", "User");
            Toast.makeText(this, "Welcome Back !", Toast.LENGTH_SHORT).show();
            Log.d("SplashCheck", "User from SharedPrefs: " + userEmail);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Log.d("SplashCheck", "Firebase fallback: " + currentUser.getEmail());
                Toast.makeText(this, "Welcome Back !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, OnboardingActivity.class));
            }
            finish();
        }
    }
}