package com.example.snapeats.ui.fragements;

//import static com.example.snapeats.repository.FoodRepository.cartFoods;

import android.annotation.SuppressLint;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartScreenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartScreenFragment() {
        // Required empty public constructor
    }

    ArrayList<FoodItemModel> foodCartModelArrayList = new ArrayList<>();
    FoodCartAdapter foodCartAdapter;
    private FoodRepository foodRepository;
    CartManager cartManager;

    static final int MAX_COUNT =15;
    static final int deliveryfeevalue = 15;
    static final int platformfeevalue = 5;
    TextView pricesummary, deliveryfee, platformfee, totalprice, finalprice;

    NestedScrollView cartlistlayout;
    LinearLayout emptyCartLayout;
    LinearLayout price_layout;
    ProgressBar loader ;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cart_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static CartScreenFragment newInstance(String param1, String param2) {
        CartScreenFragment fragment = new CartScreenFragment();
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
        foodRepository = new FoodRepository();
        cartManager = new CartManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_screen, container, false);

        cartlistlayout = view.findViewById(R.id.cartlistlayout);
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);
        price_layout = view.findViewById(R.id.price_layout);
        loader = view.findViewById(R.id.loader);

        RecyclerView recycler_food_cart = view.findViewById(R.id.cart_recyclerview);
        recycler_food_cart.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

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
                } else {
                    Toast.makeText(getContext(), "Maximum Item Limit reached", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCartDecrement(FoodItemModel model) {
                CartManager.getInstance().cartDecrement(model);
            }

            @Override
            public void onCartRemove(FoodItemModel model) {
                CartManager.getInstance().cartRemove(model);
            }
        });

        recycler_food_cart.setAdapter(foodCartAdapter);
        FetchCartData();


        return view;
    }

    private void FetchCartData() {
        if (!NetworkUtils.isInternetAvailable(getContext())) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new NoInternetScreen())
                        .addToBackStack(null)  // taaki retry pe back stack se pichla fragment aaye
                        .commit();
            }
            return;
        }
        loader.setVisibility(View.VISIBLE);
        emptyCartLayout.setVisibility(View.GONE);
        cartlistlayout.setVisibility(View.GONE);
        price_layout.setVisibility(View.GONE);

        foodRepository.fetchCartFoods(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override public void onDataChange(DataSnapshot snapshot) {
                ArrayList<FoodItemModel> cartfoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FoodItemModel food = child.getValue(FoodItemModel.class);
                    if (food != null) cartfoods.add(food);
                }
                Collections.reverse(cartfoods);

                loader.setVisibility(View.VISIBLE);
                cartlistlayout.setVisibility(View.GONE);
                price_layout.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    loader.setVisibility(View.GONE);
                    if (cartfoods.isEmpty()) {
                        emptyCartLayout.setVisibility(View.VISIBLE);
                        cartlistlayout.setVisibility(View.GONE);
                        price_layout.setVisibility(View.GONE);
                    } else {
                        emptyCartLayout.setVisibility(View.GONE);
                        cartlistlayout.setVisibility(View.VISIBLE);
                        price_layout.setVisibility(View.VISIBLE);

                        // Update the Cart food list
                        foodCartAdapter.updateData(cartfoods);

                        // Calculate Total Price
                        int itemPrice = cartManager.calculateTotalPrice(cartfoods);
                        int finalPrice = itemPrice + deliveryfeevalue + platformfeevalue;

                        pricesummary.setText("₹" + itemPrice);
                        deliveryfee.setText("₹" + deliveryfeevalue);
                        platformfee.setText("₹" + platformfeevalue);
                        totalprice.setText("₹" + (itemPrice + deliveryfeevalue + platformfeevalue));
                        finalprice.setText("₹" + finalPrice);
                    }
                }, 800);
            }
            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
//                    for (Food_Item_Model item : cartfoods) {
//                        Log.d("item", item.getFood_name());
//                    }
//new Handler().postDelayed(() -> loader.setVisibility(View.GONE), 1000);