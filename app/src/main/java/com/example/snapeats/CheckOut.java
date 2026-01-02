package com.example.snapeats;

import static com.example.snapeats.data.managers.ProfileManager.getcurrentuser;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapeats.data.managers.CartManager;
import com.example.snapeats.data.managers.ProfileManager;
import com.example.snapeats.data.models.AddressModel;
import com.example.snapeats.data.models.CategoriesModel;
import com.example.snapeats.data.models.FoodItemModel;
import com.example.snapeats.ui.adapters.CheckoutAddressAdapter;
import com.example.snapeats.ui.bottomsheets.AddAddressBottomSheet;
import com.example.snapeats.utils.SnapEatsApplication;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckOut extends AppCompatActivity {

    MaterialCardView cardCOD, cardCard, cardUPI;
    MaterialRadioButton radioCOD, radioCard, radioUPI;
    MaterialButton btnPlaceOrder,add_address;
    RecyclerView recyclerView;
    String selectedPayment = "COD";
    CheckoutAddressAdapter adapter;
    ArrayList<FoodItemModel> cartList = null;

    TextView totalprice;
    TextView pricesummary;

    private List<AddressModel> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        loadUserAddresses();
        cardCOD = findViewById(R.id.cardCOD);
        cardCard = findViewById(R.id.cardCard);
        cardUPI = findViewById(R.id.cardUPI);
        totalprice = findViewById(R.id.totalprice);
        pricesummary = findViewById(R.id.pricesummary);
        recyclerView = findViewById(R.id.checkout_add);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        add_address = findViewById(R.id.add_address);

        radioCOD = findViewById(R.id.radioCOD);
        radioCard = findViewById(R.id.radioCard);
        radioUPI = findViewById(R.id.radioUPI);

        ImageButton backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> {
            finish();
        });

        selectPayment("COD");

        int finalPrice = getIntent().getIntExtra("Food_price", 0);
        int itemPrice = getIntent().getIntExtra("Item_price", 0);
        String cart_json = getIntent().getStringExtra("cart_json");

        totalprice.setText(String.valueOf(finalPrice));
        pricesummary.setText(String.valueOf(itemPrice));


        if (cart_json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FoodItemModel>>() {
            }.getType();
            cartList = gson.fromJson(cart_json, type);
        }

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAddressBottomSheet sheet = new AddAddressBottomSheet();
                sheet.show(getSupportFragmentManager(), "address_sheet");
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSimpleOrder(cartList);

                CartManager.getInstance().clearUserCart();

                adapter.notifyDataSetChanged();
            }
        });

        cardCOD.setOnClickListener(v -> selectPayment("COD"));
        cardCard.setOnClickListener(v -> selectPayment("CARD"));
        cardUPI.setOnClickListener(v -> selectPayment("UPI"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressList = new ArrayList<>();
        adapter = new CheckoutAddressAdapter(this, addressList, new CheckoutAddressAdapter.OnAddressClick() {
            @Override
            public void onEditClick(AddressModel item, int position) {
                openEditAddressBottomSheet(item);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);

    }

    public void openEditAddressBottomSheet(AddressModel address) {
        AddAddressBottomSheet bottomSheet = AddAddressBottomSheet.newInstance(address);
        bottomSheet.show(getSupportFragmentManager(), "edit_address_sheet");
    }

    private void selectPayment(String method) {
        resetCard(radioCOD);
        resetCard(radioCard);
        resetCard(radioUPI);

        // Apply selected
        switch (method) {
            case "COD":
                applySelected(radioCOD);
                break;

            case "CARD":
                applySelected(radioCard);
                break;

            case "UPI":
                applySelected(radioUPI);
                break;
        }

        selectedPayment = method;
    }

    private void resetCard( MaterialRadioButton radio) {
        radio.setChecked(false);
    }

    private void applySelected(MaterialRadioButton radio) {
        radio.setChecked(true);
    }

    private void loadUserAddresses() {

        //showLoader(); // show loader immediately

        String currentUserId = getcurrentuser().getUid();
        if (currentUserId == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserId)
                .child("addresses");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Delay to show loader smoothly
                new android.os.Handler().postDelayed(() -> {

                    addressList.clear();

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                            AddressModel address = addressSnapshot.getValue(AddressModel.class);
                            if (address != null) {
                                addressList.add(address);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        //showSavedAddresses();   // show address layout

                    } else {
                        //showEmptyState();       // show empty layout
                    }

                }, 800); // 800ms = smooth loader visible, like Swiggy

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CheckOut.this, "Failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                //showEmptyState();
            }
        });
    }

    public void saveSimpleOrder(ArrayList<FoodItemModel> cartFoodList) {

        String uid = ProfileManager.getcurrentuser().getUid();

        DatabaseReference ordersRef = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("orders");

        Map<String, Object> orderMap = new HashMap<>();

        for (FoodItemModel food : cartFoodList) {
            orderMap.put(food.getId(), food); // ✅ directly add
        }


        ordersRef.updateChildren(orderMap)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this,"Order Place Successfully!!",Toast.LENGTH_SHORT).show()
                        )
                .addOnFailureListener(e ->
                        Log.e("Order", "❌ Append failed", e));

    }

}
