package com.example.snapeats.data.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Simple class to check Firebase Realtime Database connection status.
 * Uses ".info/connected" to detect if the app is connected to Firebase.
 */
public class FireBaseConnection {

    /**
     * Listener interface for connection status callbacks.
     */
    public interface FirebaseConnectionListener {
        void onConnected();    // Called when connected to Firebase
        void onDisconnected(); // Called when disconnected
    }

    // Track the last known state to detect changes
    private static Boolean lastConnectionState = null; // Null means initial state
    private static ValueEventListener activeListener = null; // To allow removal if needed

    /**
     * Checks the Firebase connection and notifies the listener only on state changes.
     * Skips initial "disconnected" if it's the first check.
     * @param listener Callback for connection status.
     */
    public static void checkFirebaseConnection(FirebaseConnectionListener listener) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        // Remove any existing listener to avoid duplicates
        if (activeListener != null) {
            connectedRef.removeEventListener(activeListener);
        }

        activeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected == null) return; // Rare edge case

                if (lastConnectionState == null) {
                    // Initial state: Only notify if connected; skip initial disconnected
                    lastConnectionState = connected;
                    if (connected) {
                        listener.onConnected();
                    }
                    return;
                }

                // Notify only on change
                if (connected && !lastConnectionState) {
                    listener.onConnected();
                } else if (!connected && lastConnectionState) {
                    listener.onDisconnected();
                }
                lastConnectionState = connected;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                if (lastConnectionState == null || lastConnectionState) {
                    listener.onDisconnected();
                }
                lastConnectionState = false;
            }
        };
        connectedRef.addValueEventListener(activeListener);
    }

    /**
     * Call this when you want to stop listening (e.g., in onDestroy).
     */
    public static void removeConnectionListener() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        if (activeListener != null) {
            connectedRef.removeEventListener(activeListener);
            activeListener = null;
            lastConnectionState = null;
        }
    }
}
