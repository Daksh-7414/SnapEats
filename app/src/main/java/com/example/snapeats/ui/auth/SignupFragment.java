package com.example.snapeats.ui.auth;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.snapeats.R;
import com.example.snapeats.utils.SnapEatsApplication;
import com.example.snapeats.data.models.UsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

public class SignupFragment extends Fragment {

    private static final String TAG = "SignupFragment";

    // Fragment parameter arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    // UI components
    private AppCompatButton btnSignUp;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private TextView signinText;
    private ImageButton googlebtn;
    private ImageButton facebookbtn;
    private LinearLayout checkTerm;
    private TextView termsText;
    private MaterialCheckBox checkbox;

    // Reference to parent AuthActivity for communication
    private AuthActivity authActivity;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of SignupFragment
     */
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve fragment arguments if any
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Get reference to parent AuthActivity for callback methods
        if (getActivity() instanceof AuthActivity) {
            authActivity = (AuthActivity) getActivity();
        }
        // Initialize Firebase Auth instance
        authActivity.GetAuthObj();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Initialize UI components
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    /**
     * Initializes all UI components from the layout
     */
    private void initializeViews(View view) {
        btnSignUp = view.findViewById(R.id.btnSignIn);
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        signinText = view.findViewById(R.id.signinText);
        googlebtn = view.findViewById(R.id.googlebtn);
        facebookbtn = view.findViewById(R.id.facebookbtn);
        checkTerm = view.findViewById(R.id.checkTerm);
        termsText = view.findViewById(R.id.termsText);
        checkbox = view.findViewById(R.id.checkbox);
    }

    /**
     * Sets up all click listeners for the fragment
     */
    private void setupClickListeners() {
        // Sign up button click listener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(!checkbox.isChecked());
            }
        });

        // Switch to login fragment text click listener
        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authActivity != null) {
                    authActivity.loadLoginFragment();
                }
            }
        });

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authActivity.startGoogleSignIn();
            }
        });
        facebookbtn.setOnClickListener(v -> Toast.makeText(getContext(), "Facebook login coming soon", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in and update UI accordingly
        FirebaseUser currentUser = authActivity.GetAuthObj().getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }

    /**
     * Handles user registration with email and password
     * Performs validation and creates new user account in Firebase
     */
    private void signUpUser() {
        // Get input values and trim whitespace
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate user inputs
        if (!validateInputs(name, email, password)) {
            return;
        }

        // Show loading state during registration
        setLoadingState(true);

        // Create user with Firebase Authentication
        authActivity.GetAuthObj().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Restore button state regardless of outcome
                        setLoadingState(false);

                        if (task.isSuccessful()) {
                            // Sign up success
                            handleSignUpSuccess(task, name, email, password);
                        } else {
                            // Sign up failed
                            handleSignUpFailure(task);
                        }
                    }
                });
        }

    /**
     * Validates user input fields
     * Returns true if all validations pass, false otherwise
     */
    private boolean validateInputs(String name, String email, String password) {
        // Name validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return false;
        }

        // Email validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        // Basic email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password should be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        if(!checkbox.isChecked()){
            checkbox.setError("Please accept Terms & Conditions");
            checkbox.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Handles successful user registration
     * Saves user data to Firebase Database and triggers authentication success
     */
    private void handleSignUpSuccess(@NonNull Task<AuthResult> task, String name, String email, String password) {
        Log.d(TAG, "createUserWithEmail:success");
        FirebaseUser user = authActivity.GetAuthObj().getCurrentUser();

        if (user != null) {
            // Update user profile with display name
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Log.d(TAG, "User profile updated with display name: " + name);

                            // Now save user data to Firebase Realtime Database
                            saveUserToDatabase(task, name, email, password);

                            // Show success message
                            Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();

                            // Notify parent activity about authentication success
                            if (authActivity != null) {
                                authActivity.onAuthenticationSuccess(user.getUid(), user.getEmail(), name);
                            }
                        } else {
                            // Handle profile update failure
                            Log.w(TAG, "Failed to update user profile", updateTask.getException());
                            // You might still want to proceed with database save even if profile update fails
                            saveUserToDatabase(task, name, email, password);
                            Toast.makeText(getContext(), "Account created but failed to set display name", Toast.LENGTH_SHORT).show();
                            if (authActivity != null) {
                                authActivity.onAuthenticationSuccess(user.getUid(), user.getEmail(), name);
                            }
                        }
                    });
        }
    }

    /**
     * Saves user data to Firebase Realtime Database
     */
    private void saveUserToDatabase(@NonNull Task<AuthResult> task, String name, String email, String password) {
        try {
            UsersModel users = new UsersModel(name, email, password);
            String id = task.getResult().getUser().getUid();
            DatabaseReference ref = SnapEatsApplication.getFirebaseDatabase()
                    .getReference()
                    .child("Users").child(id);
            ref.setValue(users);
            Log.d(TAG, "User data saved to database");
        } catch (Exception e) {
            Log.e(TAG, "Error saving user data to database: " + e.getMessage());
        }
    }

    /**
     * Handles registration failure
     * Displays error message to user
     */
    private void handleSignUpFailure(@NonNull Task<AuthResult> task) {
        Log.w(TAG, "createUserWithEmail:failure", task.getException());
        String errorMessage = "Registration failed. Please try again.";

        // Provide specific error messages for common cases
        if (task.getException() != null) {
            String exceptionMessage = task.getException().getMessage();
            if (exceptionMessage.contains("email address is already in use")) {
                errorMessage = "This email is already registered. Please use a different email or login.";
            } else if (exceptionMessage.contains("invalid email")) {
                errorMessage = "Please enter a valid email address.";
            } else {
                errorMessage = exceptionMessage;
            }
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        updateUI(null);
    }

    /**
     * Updates UI state during loading operations
     */
    private void setLoadingState(boolean isLoading) {
        btnSignUp.setEnabled(!isLoading);
        btnSignUp.setText(isLoading ? "Creating Account..." : "Sign Up");
    }

    /**
     * Updates UI based on authentication state
     * Called when user state changes (sign in/sign out)
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in - this should be handled by activity
            Log.d(TAG, "User already signed in: " + user.getEmail());
        } else {
            // User is signed out - clear sensitive fields
            etPassword.setText("");
            // Keep name and email for user convenience
        }
    }
}