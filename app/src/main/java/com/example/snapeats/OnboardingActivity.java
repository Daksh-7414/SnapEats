package com.example.snapeats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import com.example.snapeats.adapters.OnboardingAdapter;
import com.example.snapeats.models.OnboardingItem;
import com.example.snapeats.ui.MainActivity;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private AppCompatButton nextBtn;
     TextView skip_btn;
    private OnboardingAdapter adapter;
    private List<OnboardingItem> onboardingItems;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager2 = findViewById(R.id.onboardingViewPager);
        nextBtn = findViewById(R.id.nextBtn);

        setupOnboardingItems();

        adapter = new OnboardingAdapter(onboardingItems);
        viewPager2.setAdapter(adapter);

        WormDotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);
        dotsIndicator.attachTo(viewPager2);

        nextBtn.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            } else {
                // ✅ Last Screen → Go to Login/Signup
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
        skip_btn = findViewById(R.id.skip_text);

// Skip Button Click
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // yaha "OnboardingActivity.this" use karo
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setupOnboardingItems() {
        onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.slide_1,
                "All your favorites", "Get all your foods in one place. Save time and order in a few taps!"));

        onboardingItems.add(new OnboardingItem(R.drawable.slide_2,
                "Cravings? We Got You", "Explore a variety of dishes that match your cravings any time of the day."));

        onboardingItems.add(new OnboardingItem(R.drawable.slide_3,
                "Fast Delivery", "Your order will be at your door before you know it. Hot, fresh, and on time!"));
    }
}