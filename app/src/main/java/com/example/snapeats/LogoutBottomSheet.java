package com.example.snapeats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import com.example.snapeats.auth.AuthActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class LogoutBottomSheet extends BottomSheetDialogFragment {

    private AppCompatButton cancel_button;
    private AppCompatButton confirm_button;

    public LogoutBottomSheet() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_out, container, false);

        cancel_button = view.findViewById(R.id.cancel_button);
        confirm_button = view.findViewById(R.id.confirm_button);

        cancel_button.setOnClickListener(v -> dismiss());

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthActivity.logout(getContext());
                dismiss();
                Intent intent = new Intent(getContext(), OnboardingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}