package com.example.snapeats.ui.fragements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.snapeats.ui.bottomsheets.LogoutBottomSheet;
import com.example.snapeats.data.managers.ProfileManager;
import com.example.snapeats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileScreenFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView userName;
    private TextView userMail;
    private ImageView profileImage;
    private LinearLayout logOut;
    private LinearLayout editProfile;
    private LinearLayout address;
    private LinearLayout payment;
    private LinearLayout orderhistory;
    NestedScrollView ProfileLayout;
    ProgressBar loader;
    private Handler handler = new Handler();

    public ProfileScreenFragment() {
        // Required empty public constructor
    }

    public static ProfileScreenFragment newInstance(String param1, String param2) {
        ProfileScreenFragment fragment = new ProfileScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        initializeViews(view);
        setupLoader();
        setupClickListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserData();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Remove the Handler callbacks to prevent memory leaks.
        handler.removeCallbacksAndMessages(null);
    }

    private void initializeViews(View view) {
        loader = view.findViewById(R.id.loader);
        ProfileLayout = view.findViewById(R.id.ProfileLayout);
        userName = view.findViewById(R.id.userName);
        userMail = view.findViewById(R.id.userMail);
        logOut = view.findViewById(R.id.logOut);
        editProfile = view.findViewById(R.id.editProfile);
        address = view.findViewById(R.id.address);
        payment = view.findViewById(R.id.payment);
        orderhistory = view.findViewById(R.id.orderhistory);
        profileImage = view.findViewById(R.id.profileImage);
    }

    private void setupLoader() {
        loader.setVisibility(View.VISIBLE);
        ProfileLayout.setVisibility(View.GONE);


        handler.postDelayed(() -> {
            loader.setVisibility(View.GONE);
            ProfileLayout.setVisibility(View.VISIBLE);
            loadUserData();
        }, 500);
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.getDisplayName());
            userMail.setText(currentUser.getEmail());
            Uri photoUri = currentUser.getPhotoUrl();
            if(photoUri != null){
                Glide.with(this)
                        .load(photoUri)
                        .placeholder(R.drawable.no_image) // loading ke time
                        .error(R.drawable.no_image)
                        .circleCrop()
                        .into(profileImage);
            }
        } else {
            userName.setText("Guest User");
            userMail.setText("");
        }
    }

    private void refreshUserData() {

        loader.setVisibility(View.VISIBLE);
        ProfileLayout.setVisibility(View.GONE);

        handler.postDelayed(() -> {
            loader.setVisibility(View.GONE);
            ProfileLayout.setVisibility(View.VISIBLE);
            loadUserData();
        }, 300);
    }

    private void setupClickListeners(View view) {
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutBottomSheet logoutBottomSheet = new LogoutBottomSheet();
                logoutBottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), getTag());
            }
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "edit_profile");
            startActivity(intent);
        });

        address.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "address");
            startActivity(intent);
        });

        payment.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "payment");
            startActivity(intent);
        });

        orderhistory.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "order_history");
            startActivity(intent);
        });
    }
}