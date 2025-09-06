package com.example.snapeats.fragements;

//import static com.example.snapeats.fragements.cart_screen.gotocart;
//import static com.example.snapeats.repository.FoodRepository.cartFoods;
//import static com.example.snapeats.repository.FoodRepository.wishlistFoods;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.example.snapeats.managers.CartManager;
import com.example.snapeats.managers.WishlistManager;
import com.example.snapeats.repository.FoodRepository;
import com.example.snapeats.ui.Food_Detailed_Screen;
import com.example.snapeats.R;
import com.example.snapeats.adapters.Wishlist_Food_Adapter;
import com.example.snapeats.interfaces.OnFoodItemActionListener;
import com.example.snapeats.models.Food_Item_Model;
import com.example.snapeats.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

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
    private FoodRepository foodRepository;
    private WishlistManager wishlistManager;

    NestedScrollView wishlistlayout;
    LinearLayout emptyWishlistLayout;
    ProgressBar loader ;

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
        foodRepository = new FoodRepository();
        wishlistManager = new WishlistManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist_screen, container, false);

        wishlistlayout = view.findViewById(R.id.wishlistlayout);
        emptyWishlistLayout = view.findViewById(R.id.emptyWishlistLayout);
        loader = view.findViewById(R.id.loader);

        RecyclerView recycler_wishlist_food = view.findViewById(R.id.wishlist_list);
        recycler_wishlist_food.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        wishlist_food_adapter = new Wishlist_Food_Adapter(getContext(), new OnFoodItemActionListener() {
            @Override
            public void onAddToCart(Food_Item_Model model) {
                if (!model.isInCart()){
                    CartManager.getInstance().addToCart(model);
                    Toast.makeText(view.getContext(), "Item Add to Cart", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(view.getContext(), "Item Already in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleWishlist(Food_Item_Model model, int position) {
                if (model.isInWishlist()) {
                    wishlistManager.removeWishlist(model);
                } else {
                    wishlistManager.addWishlist(model);
                }
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
        FetchWishlistData();

        return view;
    }

    private void FetchWishlistData() {
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
        emptyWishlistLayout.setVisibility(View.GONE);
        wishlistlayout.setVisibility(View.GONE);

        foodRepository.fetchWishlistFoods(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Food_Item_Model> wishlistfoods = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Food_Item_Model food = child.getValue(Food_Item_Model.class);
                    if (food != null) wishlistfoods.add(food);
                }
                Collections.reverse(wishlistfoods);

                loader.setVisibility(View.VISIBLE);
                wishlistlayout.setVisibility(View.GONE);

                new Handler().postDelayed(()->{
                    loader.setVisibility(View.GONE);
                    if (wishlistfoods.isEmpty()){
                        emptyWishlistLayout.setVisibility(View.VISIBLE);
                        wishlistlayout.setVisibility(View.GONE);
                    }else {
                        emptyWishlistLayout.setVisibility(View.GONE);
                        wishlistlayout.setVisibility(View.VISIBLE);

                        wishlist_food_adapter.updateData(wishlistfoods);
                    }
                },800);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}