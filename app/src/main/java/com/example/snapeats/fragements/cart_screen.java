package com.example.snapeats.fragements;

import static com.example.snapeats.repository.FoodRepository.cartFoods;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapeats.R;
import com.example.snapeats.adapters.Food_Cart_Adapter;
import com.example.snapeats.interfaces.OnCartActionListener;
import com.example.snapeats.models.Food_Cart_Model;
import com.example.snapeats.models.Food_Item_Model;
import com.example.snapeats.repository.FoodRepository;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link cart_screen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cart_screen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public cart_screen() {
        // Required empty public constructor
    }

    ArrayList<Food_Item_Model> foodCartModelArrayList = new ArrayList<>();
    Food_Cart_Adapter foodCartAdapter;

    static int deliveryfeevalue = 15;
    static int platformfeevalue = 5;
    TextView itempriceView, deliveryfeeView, platformfeeView,totalprice, finalpriceView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cart_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static cart_screen newInstance(String param1, String param2) {
        cart_screen fragment = new cart_screen();
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
        View view = inflater.inflate(R.layout.fragment_cart_screen, container, false);

        TextView empty_cart_text = view.findViewById(R.id.empty_cart_text);
        LinearLayout cart_itmes_layout = view.findViewById(R.id.linearLayout6);
        LinearLayout price_layout = view.findViewById(R.id.price_layout);

        RecyclerView recycler_food_cart = view.findViewById(R.id.cart_recyclerview);
        recycler_food_cart.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        itempriceView = view.findViewById(R.id.itemprice);
        deliveryfeeView = view.findViewById(R.id.delivery_price);
        platformfeeView = view.findViewById(R.id.platform_fee);
        totalprice = view.findViewById(R.id.totalprice);
        finalpriceView = view.findViewById(R.id.finalprice);

        foodCartAdapter = new Food_Cart_Adapter(getContext(), cartFoods, new OnCartActionListener() {
            @Override
            public void onCartIncrement(Food_Item_Model model) {
                if (model.cart_count < 10) {
                    model.cart_count++;
                    foodCartAdapter.notifyDataSetChanged(); // or use DiffUtil
                    calculateTotalPrice();
                } else {
                    Toast.makeText(getContext(), "Maximum Item Limit reached", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCartDecrement(Food_Item_Model model) {
                model.cart_count--;
                foodCartAdapter.notifyDataSetChanged();
                calculateTotalPrice();
            }

            @Override
            public void onCartRemove(Food_Item_Model model, int position) {
                cartFoods.remove(model);
                foodCartAdapter.notifyItemRemoved(position);
                calculateTotalPrice();
            }
        });

        recycler_food_cart.setAdapter(foodCartAdapter);

        return view;
    }
    public void cart_total(int final_price){
        int finalAmount = final_price + deliveryfeevalue + platformfeevalue;

        if (itempriceView != null) {
            itempriceView.setText("₹" + final_price);
        }
        if (deliveryfeeView != null) {
            deliveryfeeView.setText("₹" + deliveryfeevalue);
        }
        if (platformfeeView != null) {
            platformfeeView.setText("₹" + platformfeevalue);
        }
        if (totalprice != null){
            totalprice.setText("₹"+finalAmount);
        }
        if (finalpriceView != null) {
            finalpriceView.setText("₹" + finalAmount);
        }
    }

    private void calculateTotalPrice() {
        int total = 0;
        for (Food_Item_Model model : cartFoods) {
            int price = Integer.parseInt(model.price.replace("₹", "").trim());
            total += price * model.cart_count;
        }

        // Handle empty cart UI
        if (getView() != null) {
            TextView empty_cart_text = getView().findViewById(R.id.empty_cart_text);
            LinearLayout cart_items_layout = getView().findViewById(R.id.linearLayout6);
            LinearLayout price_layout = getView().findViewById(R.id.price_layout);

            if (total == 0) {
                empty_cart_text.setVisibility(View.VISIBLE);
                cart_items_layout.setVisibility(View.GONE);
                price_layout.setVisibility(View.GONE);
            } else {
                empty_cart_text.setVisibility(View.GONE);
                cart_items_layout.setVisibility(View.VISIBLE);
                price_layout.setVisibility(View.VISIBLE);
            }
        }

        cart_total((int) total); // cast float to int for display
    }

    public static void gotocart(Food_Item_Model model){
        if (!cartFoods.contains(model)){
            cartFoods.add(model);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (foodCartAdapter != null) {
            foodCartAdapter.notifyDataSetChanged();
            calculateTotalPrice();
        }
    }
}