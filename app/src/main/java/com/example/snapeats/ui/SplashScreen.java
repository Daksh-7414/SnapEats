package com.example.snapeats.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snapeats.OnboardingActivity;
import com.example.snapeats.R;
import com.example.snapeats.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapeats_splash);

        // Using Handler to delay splash screen display
        new Handler().postDelayed(this::checkUserAuthentication, 1000);
    }

    /**
     * Checks user authentication state and navigates accordingly
     * Priority: SharedPreferences -> Firebase Current User -> Onboarding
     */
    private void checkUserAuthentication() {

        // First check SharedPreferences for login state
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // User is logged in (via SharedPreferences) - go to MainActivity
            String userName =  sharedPreferences.getString("userName","UserName");
            String userEmail = sharedPreferences.getString("userEmail", "User");

            Toast.makeText(this, "Welcome Back !", Toast.LENGTH_SHORT).show();
            Log.d("SplashCheck", "User from SharedPrefs: " + userEmail);

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Check Firebase current user as fallback
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Firebase user exists but SharedPreferences doesn't - sync them
                Log.d("SplashCheck", "Firebase fallback: " + currentUser.getEmail());
                Toast.makeText(this, "Welcome Back !", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class));
            } else {
                // No user logged in - go to onboarding
                startActivity(new Intent(this, OnboardingActivity.class));
            }
            finish();
        }
    }
}