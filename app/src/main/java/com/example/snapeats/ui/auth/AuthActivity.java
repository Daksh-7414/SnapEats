package com.example.snapeats.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.snapeats.R;
import com.example.snapeats.utils.SnapEatsApplication;
import com.example.snapeats.ui.activities.MainActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    // Shared preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Firebase + Google Sign-In
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    // UI Components
    private ProgressBar progressBar;
    private FrameLayout authContainer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize UI
        progressBar = findViewById(R.id.progressBar);
        authContainer = findViewById(R.id.authContainer);

        // SharedPreferences init
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // FirebaseAuth init
        GetAuthObj();

        // If user already logged in via prefs, go to main
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            finish();
            return;
        }

        // load login fragment only if first time
        if (savedInstanceState == null) {
            loadLoginFragment();
        }

        // Setup Google Sign-In (GoogleSignInClient + launcher)
        setupGoogleSignIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    public FirebaseAuth GetAuthObj() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    private void setupGoogleSignIn() {
        // Make sure you have default_web_client_id in strings.xml
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // must exist
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Register ActivityResultLauncher for modern API
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    } else {
                        Log.w(TAG, "Google sign-in cancelled or failed");
                        Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Show/Hide Loading Methods
    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            authContainer.setVisibility(View.GONE);
        }
    }

    public void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            authContainer.setVisibility(View.VISIBLE);
        }
    }

    // Called from fragment: ((AuthActivity)getActivity()).startGoogleSignIn();
    public void startGoogleSignIn() {
        if (mGoogleSignInClient == null) {
            Toast.makeText(this, "GoogleSignIn not initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String idToken = account.getIdToken();
                firebaseAuthWithGoogle(idToken);
            } else {
                Log.w(TAG, "GoogleSignInAccount is null");
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed: " + e.getStatusCode(), e);
            Toast.makeText(this, "Google sign-in error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            Toast.makeText(this, "No ID token from Google", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        boolean isNewUser = task.getResult() != null &&
                                task.getResult().getAdditionalUserInfo() != null &&
                                task.getResult().getAdditionalUserInfo().isNewUser();

                        // Save prefs
                        if (user != null) {
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userEmail", user.getEmail());
                            editor.putString("userName", user.getDisplayName());
                            editor.apply();

                            // ✅ यहाँ Realtime Database में save करो
                            saveUserToRealtimeDatabase(user);
                        }

                        if (isNewUser) {
                            Log.d(TAG, "New Google user created: " + (user != null ? user.getEmail() : "unknown"));
                            // User data already saved in saveUserToRealtimeDatabase method
                        } else {
                            Log.d(TAG, "Existing Google user signed in: " + (user != null ? user.getEmail() : "unknown"));
                        }

                        Toast.makeText(this, "Welcome " + (user != null ? user.getDisplayName() : ""), Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToRealtimeDatabase(FirebaseUser user) {
        if (user == null) return;

        DatabaseReference ref = SnapEatsApplication.getFirebaseDatabase()
                .getReference()
                .child("Users");
        String userId = user.getUid();

        // User data map बनाओ
        Map<String, Object> userData = new HashMap<>();
        userData.put("emailAdd", user.getEmail());
        userData.put("userName", user.getDisplayName());
        userData.put("userPassword", "logged using google");
        userData.put("gender", "");
        userData.put("phoneNo", "");
        userData.put("image", "");

        // Realtime Database में save करो
        ref.child(userId).setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data saved to Realtime Database");
                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user data: " + e.getMessage());
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                });
    }

    // Utility methods
    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onAuthenticationSuccess(String userId, String email, String name) {
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userId", userId);
        editor.putString("userEmail", email);
        editor.putString("userName", name);
        editor.apply();
        navigateToMainActivity();
    }

    // Modified fragment loading methods with loader
    public void loadSignupFragment() {
        showLoading();

        handler.postDelayed(() -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.authContainer, new SignupFragment());
            transaction.commit();

            hideLoading();
        }, 300);
    }

    public void loadLoginFragment() {
        showLoading();

        handler.postDelayed(() -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.authContainer, new LoginFragment());
            transaction.commit();

            hideLoading();
        }, 300);
    }


    public static void logout(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply(); // apply() better hai commit() se

        FirebaseAuth.getInstance().signOut();
    }
}