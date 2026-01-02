package com.example.snapeats.ui.fragements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapeats.ui.profile.CheckOut;
import com.example.snapeats.R;
import com.example.snapeats.ui.adapters.FoodCartAdapter;
import com.example.snapeats.data.interfaces.OnCartActionListener;
import com.example.snapeats.data.managers.CartManager;
import com.example.snapeats.data.models.FoodItemModel;
import com.example.snapeats.data.repository.FoodRepository;
import com.example.snapeats.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class CartScreenFragment extends Fragment {

    ArrayList<FoodItemModel> cartfoods;

    ArrayList<FoodItemModel> foodCartModelArrayList = new ArrayList<>();
    FoodCartAdapter foodCartAdapter;
    private FoodRepository foodRepository;
    CartManager cartManager;

    static final int MAX_COUNT = 15;
    static final int deliveryfeevalue = 15;
    static final int platformfeevalue = 5;

    TextView pricesummary, deliveryfee, platformfee, totalprice, finalprice;
    int finalPrice;
    int itemPrice;

    AppCompatButton checkout_btn;

    NestedScrollView cartlistlayout;
    LinearLayout emptyCartLayout;
    LinearLayout price_layout;
    ProgressBar loader;

    public CartScreenFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodRepository = new FoodRepository();
        cartManager = new CartManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart_screen, container, false);

        cartlistlayout = view.findViewById(R.id.cartlistlayout);
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);
        price_layout = view.findViewById(R.id.price_layout);
        loader = view.findViewById(R.id.loader);
        checkout_btn = view.findViewById(R.id.checkout_btn);

        RecyclerView recycler_food_cart = view.findViewById(R.id.cart_recyclerview);
        recycler_food_cart.setLayoutManager(new LinearLayoutManager(getContext()));

        pricesummary = view.findViewById(R.id.pricesummary);
        deliveryfee = view.findViewById(R.id.delivery_fee);
        platformfee = view.findViewById(R.id.platform_fee);
        totalprice = view.findViewById(R.id.totalprice);
        finalprice = view.findViewById(R.id.finalprice);



        foodCartAdapter = new FoodCartAdapter(getContext(), new OnCartActionListener() {

            @Override
            public void onCartIncrement(FoodItemModel model) {
                if (model.cart_count < MAX_COUNT) {
                    CartManager.getInstance().cartIncrement(model);
                    updatePriceSummary();
                    foodCartAdapter.notifyDataSetChanged();  // smooth update
                } else {
                    Toast.makeText(getContext(), "Maximum Item Limit reached", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCartDecrement(FoodItemModel model) {
                if (model.cart_count > 1) {
                    CartManager.getInstance().cartDecrement(model);
                    updatePriceSummary();
                    foodCartAdapter.notifyDataSetChanged();
                } else {
                    CartManager.getInstance().cartRemove(model);
                    FetchCartData(true); // reload only when item removed
                }
            }

            @Override
            public void onCartRemove(FoodItemModel model) {
                CartManager.getInstance().cartRemove(model);
                FetchCartData(true);
            }
        });

        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String cartJson = gson.toJson(cartfoods);
                Intent intent = new Intent(getContext(), CheckOut.class);
                intent.putExtra("Food_price",finalPrice);
                intent.putExtra("Item_price",itemPrice);
                intent.putExtra("cart_json", cartJson);
                startActivity(intent);
            }
        });

        recycler_food_cart.setAdapter(foodCartAdapter);

        FetchCartData(true);
        return view;
    }


    private void FetchCartData(boolean showLoader) {

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

        // Show loader only when needed
        if (showLoader) {
            loader.setVisibility(View.VISIBLE);
            cartlistlayout.setVisibility(View.GONE);
            emptyCartLayout.setVisibility(View.GONE);
            price_layout.setVisibility(View.GONE);
        }

        foodRepository.fetchCartFoods(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                cartfoods = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) cartfoods.add(food);
                }

                Collections.reverse(cartfoods);

                new Handler().postDelayed(() -> {

                    if (showLoader) loader.setVisibility(View.GONE);

                    if (cartfoods.isEmpty()) {

                        emptyCartLayout.setVisibility(View.VISIBLE);
                        cartlistlayout.setVisibility(View.GONE);
                        price_layout.setVisibility(View.GONE);

                    } else {

                        emptyCartLayout.setVisibility(View.GONE);
                        cartlistlayout.setVisibility(View.VISIBLE);
                        price_layout.setVisibility(View.VISIBLE);

                        foodCartAdapter.updateData(cartfoods);
                        updatePriceSummary();
//                        int itemPrice = cartManager.calculateTotalPrice(cartfoods);
//                        int finalPrice = itemPrice + deliveryfeevalue + platformfeevalue;
//                        pricesummary.setText("₹" + itemPrice);
//                        deliveryfee.setText("₹" + deliveryfeevalue);
//                        platformfee.setText("₹" + platformfeevalue);
//                        totalprice.setText("₹" + finalPrice);
//                        finalprice.setText("₹" + finalPrice);
                    }

                }, showLoader ? 800 : 0);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePriceSummary() {

        if (foodCartAdapter == null) return;

        itemPrice = cartManager.calculateTotalPrice(cartfoods);
        finalPrice = itemPrice + deliveryfeevalue + platformfeevalue;
        pricesummary.setText("₹" + itemPrice);
        deliveryfee.setText("₹" + deliveryfeevalue);
        platformfee.setText("₹" + platformfeevalue);
        totalprice.setText("₹" + finalPrice);
        finalprice.setText("₹" + finalPrice);
    }
}
