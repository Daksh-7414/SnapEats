package com.example.snapeats;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_manager);
        if (savedInstanceState == null) { // first launch
            String screen = getIntent().getStringExtra("screen");
            Fragment fragmentToLoad;

            if ("edit_profile".equals(screen)) {
                fragmentToLoad = new EditProfileFragment();
            } else if ("address".equals(screen)) {
                fragmentToLoad = new ProfileAddressFragment();
            } else if ("payment".equals(screen)) {
                fragmentToLoad = new PaymentProfile();
            } else {
                fragmentToLoad = new EditProfileFragment(); // default
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profileContainer, fragmentToLoad)
                    .commit();
        }
    }

    public static FirebaseUser getcurrentuser(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser;
    }
}