package com.example.snapeats;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SnapeatsApplication extends Application {

    private static FirebaseDatabase firebaseDatabase;
    private static boolean isFirebaseConnected = false;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("SnapeatsApplication", "Application started");

        // Firebase initialize
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        // Setup global connection listener
        DatabaseReference connectedRef = firebaseDatabase.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null && connected) {
                    isFirebaseConnected = true;
                    Log.d("SnapEatsApp", "✅ Firebase Connected");
                } else {
                    isFirebaseConnected = false;
                    Log.d("SnapEatsApp", "❌ Firebase Disconnected");
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
