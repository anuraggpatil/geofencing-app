package com.example.geofencingmajorproject;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geofencingmajorproject.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment<onRequestPermissionResult> extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    private static final String TAG = "HomeFragment";
    private GoogleMap mMap;
//    private ActivityMapsBinding binding;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private int FINE_LOCATION_ACCESS_REQEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQEST_CODE = 10002;

    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    NotificationHelper notificationHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        return binding.getRoot();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        notificationHelper = new NotificationHelper(getContext());

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new GeofenceHelper(getContext());
        firestore.collection("Admin").document(auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists() && value !=null){
                    GEOFENCE_RADIUS = value.getLong("Geofence Radius");
                }
            }
        });
//        firestore.collection("Admin").document(auth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot snapshot) {
//                if(snapshot.exists()){
//                    GEOFENCE_RADIUS = snapshot.getLong("Geofence Radius");
//                }
//            }
//        });

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker in Sydney and move the camera

        ArrayList<String> connectedUsers = new ArrayList<>();

        firestore.collection("Admin").document(auth.getCurrentUser().getEmail()).collection("Users Connected").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    String user = "";
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if(documentSnapshot.getString("Request").equals("ACCEPTED")) {
                            connectedUsers.add(documentSnapshot.getId());
                            user = documentSnapshot.getId();
                        }
                    }

                    if(!user.equals("")){
                        firestore.collection("User").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                                if (snapshot != null && snapshot.exists()) {
                                        Log.d("SPECIAL", " data: " + snapshot.getData());
                                        mMap.clear();
                                        LatLng latlng = new LatLng(snapshot.getDouble("Latitude"), snapshot.getDouble("Longitude"));
                                        MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(snapshot.getString("Name")).icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24));
                                        mMap.addMarker(markerOptions);
                                        CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(latlng, 16);
                                        mMap.animateCamera(cameraupdate);

                                        if(snapshot.getDouble("Geofence Latitude")!= null) {
                                            LatLng tempLatLng = new LatLng(snapshot.getDouble("Geofence Latitude"), snapshot.getDouble("Geofence Longitude"));

                                            addMarker(tempLatLng);
                                            addCircle(tempLatLng, snapshot.getLong("Geofence Radius"));
                                            addGeofence(tempLatLng, snapshot.getLong("Geofence Radius"));
                                        }
                                } else {
                                    Log.d(TAG,  " data: null");
                                }
                            }
                        });

                        firestore.collection("User").document(user).collection("Transition").document("transition").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                                if(snapshot!=null && snapshot.exists()){
                                    if(!snapshot.getString("Transition").equals("")){
                                        Log.d("SPECIAL", "onEvent: 123 " + snapshot.getString("Transition"));
                                        switch (snapshot.getString("Transition")){
                                            case "GEOFENCE_TRANSITION_EXIT":
                                                Toast.makeText(getContext(), "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                                                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "The User has exited the Geofence", HomeFragment.class);
                                                break;
                                            case "GEOFENCE_TRANSITION_DWELL":
                                                Toast.makeText(getContext(), "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                                                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", HomeFragment.class);
                                                break;
                                            case "GEOFENCE_TRANSITION_ENTER":
                                                Toast.makeText(getContext(), "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                                                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "The User has entered the Geofence", HomeFragment.class);
                                                break;
                                        }
                                    }
                                }
                            }
                        });

                    }

//                    Log.d("SPECIAL", "onComplete: " + connectedUsers);
                }else{
                    Log.d(TAG, "onComplete: ERROR FETCHING DOCUMENTS");
                }
            }
        });




        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else { // Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // show user a dialog for displaying why the permission is needed & ask for permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // we have the permission
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // we do not have the permission
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // we have the permission
                Toast.makeText(getContext(), "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                // we do not have the permission
                Toast.makeText(getContext(), "Background Location Access Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {


        if (Build.VERSION.SDK_INT >= 29) {
            // we need background permission
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                tryAddingGeofence(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQEST_CODE);
                }
            }
        } else {
            tryAddingGeofence(latLng);
        }
    }

    private void tryAddingGeofence(LatLng latLng) {
        firestore.collection("Admin").document(auth.getCurrentUser().getEmail()).collection("Users Connected").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String userEmail="";
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if(documentSnapshot.getString("Request").equals("ACCEPTED")){
                            userEmail = documentSnapshot.getId();
                        }
                    }
                    if(!userEmail.equals("")) {
                        Map<String, Object> updatedGeofenceLocation = new HashMap<>();
                        updatedGeofenceLocation.put("Geofence Latitude", latLng.latitude);
                        updatedGeofenceLocation.put("Geofence Longitude", latLng.longitude);
                        updatedGeofenceLocation.put("Geofence Radius", GEOFENCE_RADIUS);
                        firestore.collection("User").document(userEmail).update(updatedGeofenceLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                addMarker(latLng);
                                addCircle(latLng, GEOFENCE_RADIUS);
                                addGeofence(latLng, GEOFENCE_RADIUS);
                            }
                        });
                    }
                }
            }
        });
    }

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 251, 109, 106));
        circleOptions.fillColor(Color.argb(64, 251, 109, 106));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}