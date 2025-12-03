package com.example.snapeats;

import static com.example.snapeats.ProfileManager.getcurrentuser;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snapeats.adapters.AddressAdapter;
import com.example.snapeats.models.AddressModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileAddressFragment extends Fragment {

    AppCompatButton addbtn;
    TextView locationType;
    TextView completeAdd;
    TextView PhoneNo;
    private RecyclerView addressesRecyclerView;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileAddressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileAddressFragment newInstance(String param1, String param2) {
        ProfileAddressFragment fragment = new ProfileAddressFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_address, container, false);

        ImageButton backArrow = view.findViewById(R.id.back_arrow);
        addressesRecyclerView = view.findViewById(R.id.addressesRecyclerView);
        backArrow.setOnClickListener(v -> {
            requireActivity().finish();
        });

        addbtn = view.findViewById(R.id.addbtn);
        addbtn.setOnClickListener(v -> {
            AddAddressBottomSheet sheet = new AddAddressBottomSheet();
            sheet.show(getParentFragmentManager(), "address_sheet");
        });

        locationType = view.findViewById(R.id.locationType);
        completeAdd = view.findViewById(R.id.completeAdd);
        PhoneNo = view.findViewById(R.id.PhoneNo);

        setupRecyclerView();

        loadUserAddresses();


        return view;
    }

//    private void showEmptyState() {
//        addressesRecyclerView.setVisibility(View.GONE);
//        emptyStateLayout.setVisibility(View.VISIBLE);
//    }
//
//    private void showAddressList() {
//        addressesRecyclerView.setVisibility(View.VISIBLE);
//        emptyStateLayout.setVisibility(View.GONE);
//    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh addresses when returning from AddAddressBottomSheet
        loadUserAddresses();
    }

    private void setupRecyclerView() {
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(addressList, new AddressAdapter.OnAddressClick() {
            @Override
            public void onEditClick(AddressModel item, int position) {
                openEditAddressBottomSheet(item);
                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeleteClick(AddressModel item, int position) {
                performDelete(item);
            }
        });
        addressesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addressesRecyclerView.setAdapter(addressAdapter);

        // Add item decoration for spacing
        //addressesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadUserAddresses() {
        // Get current user ID
        String currentUserId = getcurrentuser().getUid();
        if (currentUserId == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserId)
                .child("addresses");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addressList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot addressSnapshot : dataSnapshot.getChildren()) {
                        AddressModel address = addressSnapshot.getValue(AddressModel.class);
                        if (address != null) {
                            addressList.add(address);
                        }
                    }
                }

                // Update UI based on whether we have addresses
//                if (addressList.isEmpty()) {
//                    showEmptyState();
//                } else {
//                    showAddressList();
//                }

                addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load addresses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                //showEmptyState();
            }
        });
    }

    private void openEditAddressBottomSheet(AddressModel address) {
        // Create bottom sheet with address data
        AddAddressBottomSheet bottomSheet = AddAddressBottomSheet.newInstance(address);
        bottomSheet.show(getParentFragmentManager(), "edit_address_sheet");
    }

    private void performDelete(AddressModel model) {
        String currentUserId = getcurrentuser().getUid();
        String editAddressId = model.getAddressId();
        if (currentUserId == null) return;

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserId)
                .child("addresses").child(editAddressId);

                userRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Address deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete address", Toast.LENGTH_SHORT).show();
                });
    }

}