package com.example.snapeats.fragements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.snapeats.R;
import com.example.snapeats.firebase.FireBaseConnection;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.ui.MainActivity;
import com.example.snapeats.utils.NetworkUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoInternetScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoInternetScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoInternetScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoInternetScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static NoInternetScreen newInstance(String param1, String param2) {
        NoInternetScreen fragment = new NoInternetScreen();
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
        View view = inflater.inflate(R.layout.fragment_no_internet_screen, container, false);
        Button retry = view.findViewById(R.id.retryButton);
        retry.setOnClickListener(v -> {
            handleRetry();
        });


        return view;
    }

    private void handleRetry() {
        // Check if network is now available
        if (NetworkUtils.isInternetAvailable(getContext())) {
            Toast.makeText(getContext(), "Network available, retrying...", Toast.LENGTH_SHORT).show();

            // Trigger the full connection check and fetch
            FireBaseConnection.checkFirebaseConnection(new FireBaseConnection.FirebaseConnectionListener() {
                @Override
                public void onConnected() {
                    // Fetch data on success
                    FoodRepository.fetchFoods(() -> {
                        // Data fetched - navigate back or refresh (e.g., load home screen)
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).loadFrag(new home_screen(), false);
                        }
                    });
                }

                @Override
                public void onDisconnected() {
                    Toast.makeText(getContext(), "Still no connection to Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Network still unavailable - show message and stay on this screen
            Toast.makeText(getContext(), "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}