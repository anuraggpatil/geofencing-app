package com.example.geofencingmajorproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    final String TAG = "SettingsFragment";
    TextInputEditText GeofenceRadius;
    CardView usersList;
    Button LogoutBtn;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    ImageButton setGeofenceRadius;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        GeofenceRadius = view.findViewById(R.id.geofenceRadius);
        usersList = view.findViewById(R.id.usersList);
        LogoutBtn = (Button) view.findViewById(R.id.LogoutSetting);
        setGeofenceRadius = view.findViewById(R.id.setGeofenceRadius);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setGeofenceRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GeofenceRadius.getText().toString().equals("")) {
                    Map<String, Object> city = new HashMap<>();
                    city.put("Geofence Radius", Integer.parseInt(GeofenceRadius.getText().toString()));

                    firestore.collection("Admin").document(firebaseAuth.getCurrentUser().getEmail())
                            .update(city)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Toast.makeText(getContext(), "Geofence Radius Updated to " + GeofenceRadius.getText(), Toast.LENGTH_SHORT).show();
                                    GeofenceRadius.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
//                Map<String, Object> updatedGeofenceRadius = new HashMap<>();
//                updatedGeofenceRadius.put("Geofence Radius", GeofenceRadius.getText());
////                Toast.makeText(getContext(), "Geofence Radius is " + updatedGeofenceRadius.size(), Toast.LENGTH_SHORT).show();
//                firestore.collection("Admin").document(firebaseAuth.getCurrentUser().getEmail()).update(updatedGeofenceRadius).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        GeofenceRadius.setText("");
//                    }
//                });
                }
                else{
                    Toast.makeText(getContext(), "Please Enter valid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        usersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ConnectedUsersList.class));

            }
        });

        DocumentReference documentReference = firestore.collection("Admin").document(firebaseAuth.getCurrentUser().getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        GeofenceRadius.setText(String.valueOf(document.getLong("Geofence Radius").intValue()));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
                else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        return view;

    }
}