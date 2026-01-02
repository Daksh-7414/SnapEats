package com.example.snapeats.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.snapeats.R;
import com.example.snapeats.data.models.FoodItemModel;
import com.example.snapeats.data.repository.FoodRepository;
import com.example.snapeats.ui.adapters.OrderedFoodAdapter;
import com.example.snapeats.ui.fragements.NoInternetScreen;
import com.example.snapeats.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FoodRepository foodRepository;
    RecyclerView recyclerView;
    OrderedFoodAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistory newInstance(String param1, String param2) {
        OrderHistory fragment = new OrderHistory();
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
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        foodRepository = new FoodRepository();
        ImageButton backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().finish());

        recyclerView = view.findViewById(R.id.orderlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderedFoodAdapter(getContext());

        recyclerView.setAdapter(adapter);

        FetchOrderedFood();

        return view;
    }

    private void FetchOrderedFood() {

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

        foodRepository.fetchOrderedFoods(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<FoodItemModel> orderedList = new ArrayList<>();

                for (DataSnapshot foodSnap : snapshot.getChildren()) {
                    FoodItemModel food = foodSnap.getValue(FoodItemModel.class);
                    if (food != null) {
                        orderedList.add(food);
                    }
                }
                Collections.reverse(orderedList);
                if (orderedList.isEmpty()) {
                    Toast.makeText(getContext(),
                            "No orders yet", Toast.LENGTH_SHORT).show();
                }
                adapter.updateData(orderedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}