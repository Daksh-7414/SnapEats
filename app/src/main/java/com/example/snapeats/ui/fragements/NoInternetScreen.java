package com.example.snapeats.ui.fragements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.snapeats.R;

import com.example.snapeats.utils.NetworkUtils;
import com.example.snapeats.utils.SnapEatsApplication;

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

        // 1️⃣ Internet check
        if (!NetworkUtils.isInternetAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Network available, retrying...", Toast.LENGTH_SHORT).show();

        // 2️⃣ Firebase check (CORRECT condition)
        if (!SnapEatsApplication.isFirebaseConnected()) {
            Toast.makeText(getContext(), "Connecting to Firebase...", Toast.LENGTH_SHORT).show();
            return; // wait for firebase listener
        }

        // 3️⃣ Internet + Firebase OK → restore UI
        if (getActivity() == null) return;

        FragmentManager fm = getActivity().getSupportFragmentManager();

        // NoInternetFragment remove
        fm.popBackStack();

        // Reload previous fragment
        Fragment currentFragment =
                fm.findFragmentById(R.id.container);

        if (currentFragment != null) {
            fm.beginTransaction()
                    .detach(currentFragment)
                    .attach(currentFragment)
                    .commitAllowingStateLoss();
        }
    }

}