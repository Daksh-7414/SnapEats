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
import android.widget.TextView;
import android.widget.Toast;
import com.example.snapeats.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    // Fragment parameter arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // UI components
    private EditText etEmail;
    private EditText etPassword;
    private AppCompatButton btnSignIn;
    private TextView tvCreateAccount;
    private ImageButton btngoogle;
    private ImageButton facebookbtn;

    // Reference to parent AuthActivity for communication
    private AuthActivity authActivity;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of LoginFragment
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize UI components
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        return view;
    }


    private void initializeViews(View view) {
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        tvCreateAccount = view.findViewById(R.id.tvCreateAccount);
        btngoogle = view.findViewById(R.id.btngoogle);
        facebookbtn = view.findViewById(R.id.facebookbtn);
    }


    private void setupClickListeners() {
        // Sign in button click listener
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        // Switch to sign up fragment text click listener
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authActivity != null) {
                    authActivity.loadSignupFragment();
                }
            }
        });
        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authActivity.startGoogleSignIn();
            }
        });
        facebookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Facebook login coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in and update UI accordingly
        FirebaseUser currentUser = authActivity.GetAuthObj().getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }


    private void signInUser() {
        // Get input values and trim whitespace
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate user inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading state during authentication
        setLoadingState(true);

        // Sign in user with Firebase Authentication
        authActivity.GetAuthObj().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Restore button state regardless of outcome
                        setLoadingState(false);

                        if (task.isSuccessful()) {
                            // Sign in success
                            handleSignInSuccess(task);
                        } else {
                            // Sign in failed
                            handleSignInFailure(task);
                            clearForm();
                        }
                    }
                });
    }


    private boolean validateInputs(String email, String password) {
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

        return true;
    }


    private void handleSignInSuccess(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "signInWithEmail:success");
        FirebaseUser user = authActivity.GetAuthObj().getCurrentUser();

        if (user != null) {
            // Show success message
            Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

            // Notify parent activity about authentication success
            if (authActivity != null) {
                authActivity.onAuthenticationSuccess(user.getUid(), user.getEmail(), getDisplayName(user));
            }
        }
    }


    private String getDisplayName(FirebaseUser user) {

        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        } else if (user.getEmail() != null) {
            // Extract username from email (part before @)
            String email = user.getEmail();
            return email.substring(0, email.indexOf('@'));
        }
        return "User";
    }


    private void handleSignInFailure(@NonNull Task<AuthResult> task) {
        Log.w(TAG, "signInWithEmail:failure", task.getException());
        String errorMessage = "Login failed. Please try again.";



        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        updateUI(null);
    }

    /**
     * Updates UI state during loading operations
     */
    private void setLoadingState(boolean isLoading) {
        btnSignIn.setEnabled(!isLoading);
        btnSignIn.setText(isLoading ? "Signing In..." : "Sign In");
    }

    /**
     * Updates UI based on authentication state
     * Called when user state changes (sign in/sign out)
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in - this should be handled by activity
            Log.d(TAG, "User signed in: " + user.getEmail());
        } else {
            // User is signed out - clear password field for security
            etPassword.setText("");
            // Keep email for user convenience
        }
    }

    /**
     * Clears the form fields
     * Can be called when needed to reset the form
     */
    public void clearForm() {
        if (etEmail != null) etEmail.setText("");
        if (etPassword != null) etPassword.setText("");
        etEmail.requestFocus();
    }
}