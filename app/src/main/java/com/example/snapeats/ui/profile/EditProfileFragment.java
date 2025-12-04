package com.example.snapeats.ui.profile;

import static com.example.snapeats.data.managers.ProfileManager.getcurrentuser;

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
import com.example.snapeats.R;
import com.example.snapeats.utils.SnapEatsApplication;
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

    private Uri imageUri; // Selected image URI

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize UI components
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

        // Load user profile from Firebase
        loadUserProfile();

        // Select profile picture
        picBtn.setOnClickListener(v -> openGallery());

        // Save profile changes
        AppCompatButton saveChange = view.findViewById(R.id.saveChange);
        saveChange.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Change Profile Details")
                    .setMessage("Are you sure you want to update your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        updateProfile();   // Update profile text fields
                        uploadImage();     // Upload profile picture to Cloudinary
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });

        return view;
    }

    /**
     * Opens gallery for selecting an image
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Handles result from gallery image picker
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // Selected image URI

                    // Show preview locally
                    Glide.with(requireContext())
                            .load(imageUri)
                            .circleCrop()
                            .into(profile_image);

                    Log.d("EditProfile", "Image selected: " + imageUri.toString());
                }
            });

    /**
     * Uploads the profile image to Cloudinary
     */
    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("EditProfile", "Cloudinary upload starting...");

        // Convert URI to File
        File imageFile = getFileFromUri(imageUri);
        if (imageFile == null) {
            Toast.makeText(getContext(), "Unable to process image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start Cloudinary upload
        MediaManager.get().upload(Uri.fromFile(imageFile))
                .option("folder", "snapeats_profiles") // Cloudinary folder
                .option("public_id", "user_" + FirebaseAuth.getInstance().getUid()) // Unique image name
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload started: " + requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytesUploaded, long totalBytes) {
                        if (totalBytes > 0) {
                            int progress = (int) ((bytesUploaded * 100) / totalBytes);
                            Log.d("Cloudinary", "Upload progress: " + progress + "%");
                        }
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d("Cloudinary", "Upload successful!");

                        // Get Cloudinary URL
                        String secureUrl = (String) resultData.get("secure_url");

                        // Save image URL in Firebase
                        saveImageUrlToFirebase(secureUrl);

                        Toast.makeText(getContext(), "Profile Image Updated!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Upload failed: " + error.getDescription());
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d("Cloudinary", "Upload rescheduled: " + requestId);
                    }
                })
                .dispatch();
    }

    /**
     * Converts URI to File (required for Cloudinary upload)
     */
    private File getFileFromUri(Uri uri) {
        try {
            String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getCacheDir(), fileName);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

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
            Log.e("EditProfile", "Error creating file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves Cloudinary image URL to Firebase Database and Firebase Auth
     */
    private void saveImageUrlToFirebase(String imageUrl) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // Save to Firebase Realtime Database
        SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid)
                .child("image")
                .setValue(imageUrl);

        // Update Firebase Auth user profile photo
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageUrl))
                .build();

        getcurrentuser().updateProfile(profileUpdates);
    }

    /**
     * Loads user profile data from Firebase
     */
    private void loadUserProfile() {
        if (getcurrentuser() == null) return;

        String uid = getcurrentuser().getUid();

        etName.setText(getcurrentuser().getDisplayName());
        etEmail.setText(getcurrentuser().getEmail());

        Uri photoUri = getcurrentuser().getPhotoUrl();
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

                    etPhone.setText(phone);

                    if (gender != null) {
                        if (gender.equals("Male")) rbMale.setChecked(true);
                        else if (gender.equals("Female")) rbFemale.setChecked(true);
                        else rbOther.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Updates user profile text fields (Name, Phone, Gender)
     */
    private void updateProfile() {
        String uid = getcurrentuser().getUid();
        String newName = etName.getText().toString();
        String newPhone = etPhone.getText().toString();

        // Get selected gender
        int selectedId = genderGroup.getCheckedRadioButtonId();
        String newGender = "";
        if (selectedId == R.id.rbMale) newGender = "Male";
        else if (selectedId == R.id.rbFemale) newGender = "Female";
        else if (selectedId == R.id.rbOther) newGender = "Other";

        // Update Realtime Database
        DatabaseReference ref = SnapEatsApplication.getFirebaseDatabase()
                .getReference("Users")
                .child(uid);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("userName", newName);
        updateMap.put("gender", newGender);
        updateMap.put("phoneNo", newPhone);

        ref.updateChildren(updateMap);

        // Update Firebase Auth display name
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        getcurrentuser().updateProfile(request);
    }
}
