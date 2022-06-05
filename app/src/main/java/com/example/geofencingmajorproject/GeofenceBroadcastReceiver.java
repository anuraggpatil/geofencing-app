package com.example.geofencingmajorproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReciev";
    private  FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");

//        Toast.makeText(context, "Geofence Triggered...", Toast.LENGTH_SHORT);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for(Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
//        firestore.collection("Admin").document(auth.getCurrentUser().getEmail()).collection("Users Connected").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    String user = "";
//                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                        user = documentSnapshot.getId();
//                    }
//                    firestore.collection("User").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
//                            if(snapshot != null && snapshot.exists()){
//                                if(snapshot.getString("Transition") != null){
//                                    switch (snapshot.getString("Transition")){
//                                        case "GEOFENCE_TRANSITION_EXIT":
//                                            Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
//                                            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "The User has exited the Geofence", HomeFragment.class);
//                                            break;
//                                        case "GEOFENCE_TRANSITION_DWELL":
//                                            Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                                            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", HomeFragment.class);
//                                            break;
//                                        case "GEOFENCE_TRANSITION_ENTER":
//                                            Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
//                                            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "The User has entered the Geofence", HomeFragment.class);
//                                            break;
//
//                                    }
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        });


        int transitionType = geofencingEvent.getGeofenceTransition();

        switch(transitionType){

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "You have exited the Geofence", HomeFragment.class);
                break;
//            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", HomeFragment.class);
//                break;
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "You have entered the Geofence", HomeFragment.class);
                break;

        }
    }
}