package com.example.snapeats;

import static com.example.snapeats.ProfileManager.getcurrentuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    // UI Components
    TextInputEditText etName, etEmail, etPhone;
    RadioGroup genderGroup;
    MaterialRadioButton rbMale, rbFemale, rbOther;
    ImageView profile_image;
    ImageButton picBtn;

    private Uri imageUri; // Selected image ka URI

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // UI elements find karo
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        genderGroup = view.findViewById(R.id.genderGroup);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);
        rbOther = view.findViewById(R.id.rbOther);
        picBtn = view.findViewById(R.id.picBtn);
        profile_image = view.findViewById(R.id.profile_image);

        // Back arrow
        ImageButton backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> requireActivity().finish());

        // User profile load karo
        loadUserProfile();

        // Profile picture select karne ka button
        picBtn.setOnClickListener(v -> openGallery());

        // Save changes button
        AppCompatButton saveChange = view.findViewById(R.id.saveChange);
        saveChange.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Change Profile Details")
                    .setMessage("Are you sure you want to update profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        updateProfile();  // Profile details update karo
                        uploadImage();    // Image Cloudinary pe upload karo
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });

        return view;
    }

    /**
     * Gallery open karta hai image select karne ke liye
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Image picker ka result handle karta hai
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // Selected image ka URI

                    // Local preview dikhao
                    Glide.with(requireContext())
                            .load(imageUri)
                            .circleCrop()
                            .into(profile_image);

                    Log.d("EditProfile", "Image selected: " + imageUri.toString());
                }
            });

    /**
     * Image ko Cloudinary pe upload karta hai
     */
    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Pehle image select karo", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EditProfile", "Cloudinary upload starting...");

        // URI ko File me convert karo
        File imageFile = getFileFromUri(imageUri);
        if (imageFile == null) {
            Toast.makeText(getContext(), "Image process nahi ho payi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cloudinary upload start karo
        MediaManager.get().upload(Uri.fromFile(imageFile))
                .option("folder", "snapeats_profiles") // Cloudinary me folder banayega
                .option("public_id", "user_" + FirebaseAuth.getInstance().getUid()) // Unique name
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload shuru hua: " + requestId);
                        // Yahan progress bar show kar sakte ho
                    }

                    @Override
                    public void onProgress(String requestId, long bytesUploaded, long totalBytes) {
                        // SAFE PROGRESS CALCULATION - zero division se bachao
                        if (totalBytes > 0) {
                            int progress = (int) ((bytesUploaded * 100) / totalBytes);
                            Log.d("Cloudinary", "Upload progress: " + progress + "%");
                        } else {
                            Log.d("Cloudinary", "Uploading: " + bytesUploaded + " bytes sent");
                        }
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d("Cloudinary", "Upload successful!");

                        // Cloudinary se URL nikal lo
                        String publicUrl = (String) resultData.get("url");
                        String secureUrl = (String) resultData.get("secure_url"); // HTTPS wala URL

                        Log.d("Cloudinary", "Public URL: " + publicUrl);
                        Log.d("Cloudinary", "Secure URL: " + secureUrl);

                        // URL Firebase me save karo
                        saveImageUrlToFirebase(secureUrl);

                        Toast.makeText(getContext(), "Profile Image Updated!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Upload fail: " + error.getDescription());
                        Toast.makeText(getContext(), "Image upload nahi hui", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d("Cloudinary", "Upload firse try hoga: " + requestId);
                    }
                })
                .dispatch(); // Upload start karo
    }

    /**
     * URI ko File me convert karta hai (Cloudinary ke liye)
     */
    private File getFileFromUri(Uri uri) {
        try {
            // Temporary file banayo cache directory me
            String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getCacheDir(), fileName);

            // URI se data copy karo file me
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            // Data copy karo
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            Log.d("EditProfile", "File created: " + file.getAbsolutePath());
            return file;

        } catch (Exception e) {
            Log.e("EditProfile", "File banane me error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cloudinary URL ko Firebase me save karta hai
     */
    private void saveImageUrlToFirebase(String imageUrl) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // 1. Firebase Database me save karo
        SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("image")  // ✅ Tumne "image" field use kiya, accha hai
                .setValue(imageUrl)
                .addOnSuccessListener(unused -> {
                    Log.d("Firebase", "Image URL save ho gayi: " + imageUrl);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "URL save nahi hui: " + e.getMessage());
                });

        // 2. Firebase Auth me bhi photo URL update karo
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageUrl))
                .build();
        Log.d("image in edit profile",imageUrl);

        getcurrentuser().updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseAuth", "Photo URL Auth me bhi update ho gaya");
                    } else {
                        Log.e("FirebaseAuth", "Auth me update nahi hua: " + task.getException());
                    }
                });
    }

    /**
     * User profile load karta hai (Firebase se)
     */
    private void loadUserProfile() {
        if (getcurrentuser() == null) return;

        String uid = getcurrentuser().getUid();
        etName.setText(getcurrentuser().getDisplayName());
        etEmail.setText(getcurrentuser().getEmail());
        Uri photoUri = getcurrentuser().getPhotoUrl();
        Log.d("rul", String.valueOf(photoUri));
        if (photoUri != null) {
            Glide.with(this)
                    .load(photoUri)
                    .circleCrop()
                    .into(profile_image);
        }

        DatabaseReference ref = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phone = snapshot.child("phoneNo").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);

                    // set phone and gender from realtime database
                    etPhone.setText(phone);
                    if (gender != null) {
                        if (gender.equals("Male")) rbMale.setChecked(true);
                        else if (gender.equals("Female")) rbFemale.setChecked(true);
                        else rbOther.setChecked(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Profile load nahi hua", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Profile details update karta hai (Name, Phone, Gender)
     */
    private void updateProfile() {
        String uid = getcurrentuser().getUid();
        String newName = etName.getText().toString();
        String newPhone = etPhone.getText().toString();

        // Gender get karo
        int selectedId = genderGroup.getCheckedRadioButtonId();
        String newGender = "";
        if (selectedId == R.id.rbMale) newGender = "Male";
        else if (selectedId == R.id.rbFemale) newGender = "Female";
        else if (selectedId == R.id.rbOther) newGender = "Other";

        // Firebase me update karo
        DatabaseReference ref = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("userName", newName);
        updateMap.put("gender", newGender);
        updateMap.put("phoneNo", newPhone);

        ref.updateChildren(updateMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        // Firebase Auth me bhi name update karo
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        getcurrentuser().updateProfile(request);

    }
}