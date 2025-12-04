package com.example.snapeats.ui.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.snapeats.R;
import com.example.snapeats.data.models.AddressModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddAddressBottomSheet extends BottomSheetDialogFragment {

    private AddressModel model;
    private boolean isEditMode = false;
    String editAddressId;

    // UI Components
    private RadioGroup locationTypeRadioGroup;
    private MaterialRadioButton homeType,workType,otherType;
    private EditText fullNameEditText, phoneNumberEditText, houseNumberEditText;
    private EditText streetEditText, cityEditText, stateEditText, pinCodeEditText;
    private Button saveAddressButton;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public AddAddressBottomSheet() {
        // Required empty public constructor
    }

    public static AddAddressBottomSheet newInstance(AddressModel model) {
        AddAddressBottomSheet fragment = new AddAddressBottomSheet();
        fragment.model = model;
        fragment.isEditMode = true;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_address_bottom_sheet, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI Components
        initViews(view);

        if (isEditMode && model != null) {
            populateFields();
//            if (deleteButton != null) {
//                deleteButton.setVisibility(View.VISIBLE);
//                deleteButton.setOnClickListener(v -> deleteAddress());
//            }
        }

        // Set up click listener for save button
        saveAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model != null) {
                    updateAddressInFirebase();
                } else {
                    saveAddressToFirebase();
                }
            }
        });

//        if (model != null) {
//            // Create AddressModel from bundle
//            model = new AddressModel();
//            if (model.getLocationType() != null) {
//                if (model.getLocationType().equals("Home")) homeType.setChecked(true);
//                else if (model.getLocationType().equals("Work")) workType.setChecked(true);
//                else otherType.setChecked(true);
//            }
//            fullNameEditText.setText(model.getFullName());
//            phoneNumberEditText.setText(model.getPhoneNumber());
//            houseNumberEditText.setText(model.getHouseNumber());
//            streetEditText.setText(model.getStreet());
//            cityEditText.setText(model.getCity());
//            stateEditText.setText(model.getState());
//            pinCodeEditText.setText(model.getPinCode());
//        }

        return view;
    }


    private void populateFields() {
        if (model == null) return;

        // Set location type
        String locationType = model.getLocationType();
        if (locationType != null) {
            switch (locationType) {
                case "Home":
                    homeType.setChecked(true);
                    break;
                case "Work":
                    workType.setChecked(true);
                    break;
                case "Other":
                    otherType.setChecked(true);
                    break;
            }
        }

        // Set other fields
        if (model.getFullName() != null) {
            fullNameEditText.setText(model.getFullName());
        }

        if (model.getPhoneNumber() != null) {
            phoneNumberEditText.setText(model.getPhoneNumber());
        }

        if (model.getHouseNumber() != null) {
            houseNumberEditText.setText(model.getHouseNumber());
        }

        if (model.getStreet() != null) {
            streetEditText.setText(model.getStreet());
        }

        if (model.getCity() != null) {
            cityEditText.setText(model.getCity());
        }

        if (model.getState() != null) {
            stateEditText.setText(model.getState());
        }

        if (model.getPinCode() != null) {
            pinCodeEditText.setText(model.getPinCode());
        }

        // Save address ID for update
        editAddressId = model.getAddressId();
    }

    private void updateAddressInFirebase() {
        if (!validateInputs()) return;

        String currentUserId = mAuth.getCurrentUser().getUid();
        if (currentUserId == null || editAddressId == null) {
            Toast.makeText(getContext(), "Error updating address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("locationType", getSelectedLocationType());
        updates.put("fullName", fullNameEditText.getText().toString().trim());
        updates.put("phoneNumber", phoneNumberEditText.getText().toString().trim());
        updates.put("houseNumber", houseNumberEditText.getText().toString().trim());
        updates.put("street", streetEditText.getText().toString().trim());
        updates.put("city", cityEditText.getText().toString().trim());
        updates.put("state", stateEditText.getText().toString().trim());
        updates.put("pinCode", pinCodeEditText.getText().toString().trim());

        // Update in Firebase
        usersRef.child(currentUserId).child("addresses").child(editAddressId)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Address updated successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void initViews(View view) {
        locationTypeRadioGroup = view.findViewById(R.id.locationTypeRadioGroup);
        fullNameEditText = view.findViewById(R.id.etName);
        phoneNumberEditText = view.findViewById(R.id.etPhone);
        houseNumberEditText = view.findViewById(R.id.etHouse);
        streetEditText = view.findViewById(R.id.etStreet);
        cityEditText = view.findViewById(R.id.etCity);
        stateEditText = view.findViewById(R.id.etState);
        pinCodeEditText = view.findViewById(R.id.etPincode);
        saveAddressButton = view.findViewById(R.id.saveAddressButton);
        homeType = view.findViewById(R.id.homeType);
        workType = view.findViewById(R.id.workType);
        otherType = view.findViewById(R.id.otherType);

    }

    private void saveAddressToFirebase() {
        // Validate input fields
        if (!validateInputs()) {
            return;
        }

        // Get current user ID
        String currentUserId = mAuth.getCurrentUser().getUid();
        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected location type
        String locationType = getSelectedLocationType();

        // Create AddressModel object
        AddressModel address = new AddressModel();
        address.setLocationType(locationType);
        address.setFullName(fullNameEditText.getText().toString().trim());
        address.setPhoneNumber(phoneNumberEditText.getText().toString().trim());
        address.setHouseNumber(houseNumberEditText.getText().toString().trim());
        address.setStreet(streetEditText.getText().toString().trim());
        address.setCity(cityEditText.getText().toString().trim());
        address.setState(stateEditText.getText().toString().trim());
        address.setPinCode(pinCodeEditText.getText().toString().trim());

        // Check if this is the first address and set as default
        checkAndSaveAddress(currentUserId, address);
    }

    private String getSelectedLocationType() {
        int selectedId = locationTypeRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "Other"; // Default value
        }

        RadioButton radioButton = getView().findViewById(selectedId);
        return radioButton.getText().toString();
    }

    private boolean validateInputs() {
        if (fullNameEditText.getText().toString().trim().isEmpty()) {
            fullNameEditText.setError("Full name is required");
            return false;
        }

        if (phoneNumberEditText.getText().toString().trim().isEmpty()) {
            phoneNumberEditText.setError("Phone number is required");
            return false;
        }

        if (houseNumberEditText.getText().toString().trim().isEmpty()) {
            houseNumberEditText.setError("House number is required");
            return false;
        }

        if (streetEditText.getText().toString().trim().isEmpty()) {
            streetEditText.setError("Street is required");
            return false;
        }

        if (cityEditText.getText().toString().trim().isEmpty()) {
            cityEditText.setError("City is required");
            return false;
        }

        if (stateEditText.getText().toString().trim().isEmpty()) {
            stateEditText.setError("State is required");
            return false;
        }

        if (pinCodeEditText.getText().toString().trim().isEmpty()) {
            pinCodeEditText.setError("PIN code is required");
            return false;
        }

        return true;
    }

    private void checkAndSaveAddress(String userId, AddressModel newAddress) {
        // Check if user already has addresses
        usersRef.child(userId).child("addresses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    // User already has addresses, add new one as non-default
                    newAddress.setDefault(false);
                    saveNewAddress(userId, newAddress);
                } else {
                    // First address, set as default
                    newAddress.setDefault(true);
                    saveNewAddress(userId, newAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error checking addresses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewAddress(String userId, AddressModel address) {
        // Generate unique address ID
        String addressId = usersRef.child(userId).child("addresses").push().getKey();
        address.setAddressId(addressId);

        // Save address to Firebase
        usersRef.child(userId).child("addresses").child(addressId).setValue(address)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Address saved successfully!", Toast.LENGTH_SHORT).show();
                    dismiss(); // Close bottom sheet
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}