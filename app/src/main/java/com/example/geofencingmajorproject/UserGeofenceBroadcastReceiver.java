package com.example.geofencingmajorproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "UserGeofenceBroadcastRe";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d("SPECIAL", "onReceive: GEOFENCE REQUEST RECEIVED");
        if(geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for(Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch(transitionType){

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                Map<String, Object> transitionUpdate = new HashMap<>();
                transitionUpdate.put("Transition", "GEOFENCE_TRANSITION_EXIT");
                FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Transition").document("transition").set(transitionUpdate);
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "The User has exited the Geofence", UserHomeFragment.class);
                break;
//            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//                Map<String, Object> transitionUpdate1 = new HashMap<>();
//                transitionUpdate1.put("Transition", "GEOFENCE_TRANSITION_DWELL");
//                FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Transition").document("transition").set(transitionUpdate1);
//                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", UserHomeFragment.class);
//                break;
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                Map<String, Object> transitionUpdate2 = new HashMap<>();
                transitionUpdate2.put("Transition", "GEOFENCE_TRANSITION_ENTER");
                FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Transition").document("transition").set(transitionUpdate2);
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "The User has entered the Geofence", UserHomeFragment.class);
                break;

        }
    }
}
