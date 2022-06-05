package com.example.geofencingmajorproject;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserHomeFragment extends Fragment implements OnMapReadyCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserHomeFragment newInstance(String param1, String param2) {
        UserHomeFragment fragment = new UserHomeFragment();
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


    private static final String TAG = "UserHomeFragment";
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;

    private GeofencingClient geofencingClient;
    private UserGeofenceHelper geofenceHelper;

    private int FINE_LOCATION_ACCESS_REQEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQEST_CODE = 10002;

    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (Build.VERSION.SDK_INT >= 29) {
//            // we need background permission
//            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                tryAddingGeofence(latLng);
//            } else {
//                if ()) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQEST_CODE);
//                } else {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQEST_CODE);
//                }
//            }
//        } else {
//
//        }

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new UserGeofenceHelper(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.userFragmentMap);

        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        firestore.collection("User").document(auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
//                                Log.d("SPECIAL", " UserHomeFragment: " + snapshot.getData());
                    if(snapshot.getDouble("Geofence Latitude") != null) {
                        LatLng latLng = new LatLng(snapshot.getDouble("Geofence Latitude"), snapshot.getDouble("Geofence Longitude"));
                        GEOFENCE_RADIUS = snapshot.getLong("Geofence Radius");
                        mMap.clear();
                        addMarker(latLng);
                        addCircle(latLng, GEOFENCE_RADIUS);
                        addGeofence(latLng, GEOFENCE_RADIUS);
                        LatLng userlatlng = new LatLng(snapshot.getDouble("Latitude"), snapshot.getDouble("Longitude"));
                        MarkerOptions markerOptions = new MarkerOptions().position(userlatlng).title("You are here").icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24)); //.icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24));
                        mMap.addMarker(markerOptions);
                    }
                } else {
                    Log.d(TAG,  " data: null");
                }
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                try {
                    mMap.clear();
                    Log.d(TAG, "onLocationChanged: LATITUDE IS " + location.getLatitude());
                    Log.d(TAG, "onLocationChanged: LONGITUDE IS " + location.getLongitude());

                    LatLng userlatlng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(userlatlng).title("You are here").icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24)); //.icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24));
                    mMap.addMarker(markerOptions);
                    CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(userlatlng, 16);
                    mMap.animateCamera(cameraupdate);

                    DocumentReference documentReference = firestore.collection("User").document(auth.getCurrentUser().getEmail());
                    Map<String, Object> updatedLocation = new HashMap<>();
                    updatedLocation.put("Latitude", location.getLatitude());
                    updatedLocation.put("Longitude", location.getLongitude());
                    documentReference.update(updatedLocation);

//                    LatLng userlatlng = new LatLng(location.getLatitude(), location.getLongitude());
//                    MarkerOptions markerOptions = new MarkerOptions().position(userlatlng).title("You are here").icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24)); //.icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_person_pin_circle_24));
//                    mMap.addMarker(markerOptions);


                } catch (Exception e) {
                    Log.d(TAG, "onLocationChanged:  ERROR " + e);
                }
            }
        };

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    private void addGeofence(LatLng latLng, float radius) {
        Log.d("SPECIAL", "addGeofence: HELPER BEING CALLED");
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}