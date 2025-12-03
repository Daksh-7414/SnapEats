package com.example.snapeats.fragements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.snapeats.LogoutBottomSheet;
import com.example.snapeats.ProfileManager;
import com.example.snapeats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileScreenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView userName;
    private TextView userMail;
    private ImageView profileImage;
    private LinearLayout logOut;
    private LinearLayout editProfile;
    private LinearLayout address;
    private LinearLayout payment;


    public ProfileScreenFragment() {
        // Required empty public constructor
    }




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile_screen.
     */
    // TODO: Rename and change types and number of parameters
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
        View view =inflater.inflate(R.layout.fragment_profile_screen, container, false);

        userName = view.findViewById(R.id.userName);
        userMail = view.findViewById(R.id.userMail);
        logOut = view.findViewById(R.id.logOut);
        editProfile = view.findViewById(R.id.editProfile);
        address = view.findViewById(R.id.address);
        payment = view.findViewById(R.id.payment);
        profileImage = view.findViewById(R.id.profileImage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.getDisplayName());
            userMail.setText(currentUser.getEmail());
            Uri photoUri = currentUser.getPhotoUrl();
            if(photoUri != null){
                Glide.with(this)
                        .load(photoUri)
                        .circleCrop()
                        .into(profileImage);
            }
        } else {
            userName.setText("Guest User");
        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutBottomSheet logoutBottomSheet = new LogoutBottomSheet();
                logoutBottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(),getTag() );
            }
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "edit_profile"); // flag for Edit Profile
            startActivity(intent);
        });

        address.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "address"); // flag for Address
            startActivity(intent);
        });

        payment.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileManager.class);
            intent.putExtra("screen", "payment"); // flag for Payment
            startActivity(intent);
        });

        return view;
    }
}