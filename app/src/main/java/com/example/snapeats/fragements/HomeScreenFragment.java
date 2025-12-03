package com.example.snapeats.fragements;

import static com.example.snapeats.ProfileManager.getcurrentuser;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.snapeats.NotificationActivity;
import com.example.snapeats.SearchScreen;
import com.example.snapeats.adapters.OfferAdapter;
import com.example.snapeats.bottomsheets.FoodDetailBottomSheet;
import com.example.snapeats.interfaces.OnCategoryActionListener;
import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.models.CategoriesModel;
import com.example.snapeats.models.OfferModel;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.R;
import com.example.snapeats.ui.ViewCategoryActivity;
import com.example.snapeats.ui.ViewPopularActivity;
import com.example.snapeats.adapters.CategoryAdapter;
import com.example.snapeats.adapters.PopularFoodAdapter;
import com.example.snapeats.adapters.RecommendedFoodAdapter;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.FoodItemModel;
import com.example.snapeats.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.util.ArrayList;

public class HomeScreenFragment extends Fragment {

    private CategoryAdapter categoryAdapter;
    private PopularFoodAdapter popularFoodAdapter;
    private RecommendedFoodAdapter recommendedFoodAdapter;
    ImageView homeProfile;
    TextView textView7;
    private ArrayList<FoodItemModel> popularFoods;
    private ArrayList<FoodItemModel> AllFoods;
    private RelativeLayout specialCard;
    ImageButton notification;
    private LinearLayout searchHome;


    private FoodRepository foodRepository;
    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodRepository = new FoodRepository();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        Toast.makeText(getContext(), "HomeScreen Loaded", Toast.LENGTH_SHORT).show();


        ProgressBar loader ;
        NestedScrollView contentLayout ;
        loader = view.findViewById(R.id.loader);
        contentLayout = view.findViewById(R.id.contentLayout);

        loader.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        // Simulate delay like network call (2 seconds)
        // Thoda sa delay for smooth experience
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);

                // Optional: Animation add kar sakte ho
                contentLayout.setAlpha(0f);
                contentLayout.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .start();
            }
        }, 300); // 0.3 second

        textView7 = view.findViewById(R.id.textView7);
        homeProfile = view.findViewById(R.id.homeProfile);
        loadProfileImage();


        ViewPager2 viewPager = view.findViewById(R.id.discountCard);

        ArrayList<OfferModel> offers = new ArrayList<>();
        offers.add(new OfferModel(R.drawable.special_image_2, "Special Offers", "Get Buy 1 Get 1 Free\n" + "on all medium\n" + "pizzas today"));
        offers.add(new OfferModel(R.drawable.combo_meal, "Special Offers", "Cool off with\n2 Drinks at ₹199\n-only this weekend"));
        offers.add(new OfferModel(R.drawable.special_image_2, "Special Offers", "Get Buy 1 Get 1 Free\non all medium\npizzas today"));
        offers.add(new OfferModel(R.drawable.combo_meal, "Special Offers", "Flat 20% Off\n" + "On all combo meals\n" + "Limited time only"));

// ✅ Adapter set
        OfferAdapter adapter = new OfferAdapter(offers);
        viewPager.setAdapter(adapter);

// 🕒 Auto-slide setup
        viewPager.setCurrentItem(adapter.getItemCount() / 2, false);

// 🕒 Auto-slide setup with smooth infinite loop
        Handler sliderHandler = new Handler(Looper.getMainLooper());

        Runnable sliderRunnable = new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                int current = viewPager.getCurrentItem();
                int next = current + 1;

                viewPager.setCurrentItem(next, true);
                sliderHandler.postDelayed(this, 4000);
            }
        };

// Start auto-slide
        sliderHandler.postDelayed(sliderRunnable, 4000);

// 🔄 Reset timer when user swipes manually
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 4000);
            }
        });

// 🌀 Carousel visual effect
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);

        if (viewPager.getChildCount() > 0) {
            viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        }

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        viewPager.setPageTransformer(transformer);

// 🧹 Prevent memory leak when fragment view destroyed
        getViewLifecycleOwner().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                sliderHandler.removeCallbacks(sliderRunnable);
            }
        });

        notification = view.findViewById(R.id.notification);
        notification.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });

        searchHome = view.findViewById(R.id.searchHome);
        searchHome.setOnClickListener(v -> {
            Gson gson = new Gson();
            String foodListJson = gson.toJson(AllFoods);
            Intent intent = new Intent(getContext(), SearchScreen.class);
            intent.putExtra("FOOD_LIST_JSON", foodListJson);
            startActivity(intent);
        });



        // Inflate the layout for this fragment
        RecyclerView recyclerView = view.findViewById(R.id.categoriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

        categoryAdapter = new CategoryAdapter(getContext(), new OnCategoryActionListener() {
            @Override
            public void onCategoryClick(CategoriesModel model) {
//                Intent intent = new Intent(getContext(), ViewCategoryActivity.class);
//                startActivity(intent);
                Gson gson = new Gson();
                String json = gson.toJson(model);
                Intent intent = new Intent(getContext(), ViewCategoryActivity.class);
                intent.putExtra("CategoryModel", json);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(categoryAdapter);

        TextView viewCategory = view.findViewById(R.id.viewCategory);
        viewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewCat = new Intent(getContext(), ViewCategoryActivity.class);
                startActivity(viewCat);
            }
        });

        TextView viewPopular = view.findViewById(R.id.viewPopularfood);
        viewPopular.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ViewPopularActivity.class);
            //startActivity(intent);
            Gson gson = new Gson();
            String json = gson.toJson(popularFoods);
            //Intent intent = new Intent(getContext(), ViewCategoryActivity.class);
            intent.putExtra("PopularFoods", json);
            startActivity(intent);
        });


        RecyclerView popularFoodRecycle = view.findViewById(R.id.popular_food_list);
        popularFoodRecycle.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        popularFoodAdapter = new PopularFoodAdapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(FoodItemModel model, int position) {
                if (model.isInWishlist()) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                popularFoodAdapter.notifyItemChanged(position, "wishlist");
            }

            @Override
            public void onFoodItemClick(FoodItemModel model) {
                FoodDetailBottomSheet bottomSheet = FoodDetailBottomSheet.newInstance(model);
                bottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "FoodDetailBottomSheet");
            }

        });
        popularFoodRecycle.setAdapter(popularFoodAdapter);

        RecyclerView recycler_recommended_food = view.findViewById(R.id.recommended_food_list);
        recycler_recommended_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        recycler_recommended_food.setNestedScrollingEnabled(false);
        recycler_recommended_food.setHasFixedSize(false);

        recommendedFoodAdapter = new RecommendedFoodAdapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(FoodItemModel model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(FoodItemModel model, int position) {
                if (model.isInWishlist()) {
                    WishlistManager.getInstance().removeWishlist(model);
                } else {
                    WishlistManager.getInstance().addWishlist(model);
                }
                recommendedFoodAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFoodItemClick(FoodItemModel model) {
                FoodDetailBottomSheet bottomSheet = FoodDetailBottomSheet.newInstance(model);
                bottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "FoodDetailBottomSheet");
            }

        });
        recycler_recommended_food.setAdapter(recommendedFoodAdapter);

        fetchCategories();
        FetchHomeData();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchCategories();
    }

    private void fetchCategories(){
        foodRepository.fetchCategories(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<CategoriesModel> categories = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    CategoriesModel category = child.getValue(CategoriesModel.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }
                // Now update adapter
                categoryAdapter.updateData(categories);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FetchHomeData() {
        if (!NetworkUtils.isInternetAvailable(getContext())) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new NoInternetScreen())
                        .addToBackStack(null) 
                        .commit();
            }
            return;
        }
        //Fetch Category from Firebase

//        public ArrayList<FoodItemModel> popularFoods ;

        foodRepository.fetchAllFoods(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AllFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) AllFoods.add(food);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Fetch Popular Foods from Firebase
        foodRepository.fetchPopularFoods(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                popularFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) popularFoods.add(food);
                }
                popularFoodAdapter.updateData(popularFoods);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Fetch Recommended Foods from Firebase
        foodRepository.fetchRecommendedFoods(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<FoodItemModel> recommendedFoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) recommendedFoods.add(food);
                }
                recommendedFoodAdapter.updateData(recommendedFoods);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Existing code ke saath bas yeh method update karo:

    /**
     * Profile image load karta hai Cloudinary URL se
     */
    private void loadProfileImage() {
        if (getcurrentuser() == null || !isAdded() || getContext() == null) {
            if (homeProfile != null) {
                homeProfile.setImageResource(R.drawable.profile);
            }
            return;
        }
        if (getcurrentuser() != null) {
            textView7.setText("Hi "+ getcurrentuser().getDisplayName());
            Uri photoUri = getcurrentuser().getPhotoUrl();
            Log.d("rul", String.valueOf(photoUri));
            if (photoUri != null) {
                Glide.with(this)
                        .load(photoUri)
                        .circleCrop()
                        .into(homeProfile);
            }
        }
    }
}
