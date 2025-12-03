package com.example.snapeats;

import android.app.Application;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SnapEatsApplication extends Application {

    private static FirebaseDatabase firebaseDatabase;
    private static boolean isFirebaseConnected = false;
    private static DatabaseReference connectedRef;
    @Override
    public void onCreate() {
        super.onCreate();

        // Cloudinary initialize karo - app start hote hi
        // Cloudinary initialize karo - CORRECT WAY
        CloudinaryConfig config = new CloudinaryConfig();
        MediaManager.init(this, config.getConfig());  // getConfig() call karo, not the object


        Log.d("SnapeatsApplication", "Application started");

        // Firebase initialize
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        // Setup global connection listener
        connectedRef = firebaseDatabase.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null && connected) {
                    isFirebaseConnected = true;
                    Log.d("SnapEatsApp", " Firebase Connected");
                } else {
                    isFirebaseConnected = false;
                    Log.d("SnapEatsApp", "Firebase Disconnected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                isFirebaseConnected = false;
                Log.w("SnapEatsApp", "Connection listener cancelled: " + error.getMessage());
            }
        });
    }

    // Global getter for Firebase instance
    public static FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }


    // Global getter for connection status
    public static boolean isFirebaseConnected() {
        return isFirebaseConnected;
    }

}
