package com.example.snapeats.fragements;

import static com.example.snapeats.fragements.cart_screen.gotocart;
import static com.example.snapeats.repository.FoodRepository.cartFoods;
import static com.example.snapeats.repository.FoodRepository.wishlistFoods;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.snapeats.ui.Food_Detailed_Screen;
import com.example.snapeats.R;
import com.example.snapeats.adapters.Wishlist_Food_Adapter;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.Food_Item_Model;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link wishlist_screen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class wishlist_screen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Wishlist_Food_Adapter wishlist_food_adapter;


    public wishlist_screen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment wishlist_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static wishlist_screen newInstance(String param1, String param2) {
        wishlist_screen fragment = new wishlist_screen();
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
        View view = inflater.inflate(R.layout.fragment_wishlist_screen, container, false);


        RecyclerView recycler_wishlist_food = view.findViewById(R.id.wishlist_list);
        recycler_wishlist_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        wishlist_food_adapter = new Wishlist_Food_Adapter(getContext(), wishlistFoods, new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!cartFoods.contains(model)){
                    model.cart_count++;
                    gotocart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                wishlistFoods.remove(position);
                wishlist_food_adapter.notifyItemRemoved(position);
                wishlist_food_adapter.notifyItemRangeChanged(position, wishlistFoods.size());

            }

            @Override
            public void onFoodItemClick(Food_Item_Model model) {
                Gson gson = new Gson();
                String json = gson.toJson(model);
                Intent intent = new Intent(getContext(), Food_Detailed_Screen.class);
                intent.putExtra("foodModel", json);
                startActivity(intent);
            }
        });
        recycler_wishlist_food.setAdapter(wishlist_food_adapter);


        return view;
    }
    public static void gotowishlist(Food_Item_Model model){
        if (!model.isInWishlist()) {
            model.setInWishlist(true); // mark as wishlist
            wishlistFoods.add(model);
        }
    }

}