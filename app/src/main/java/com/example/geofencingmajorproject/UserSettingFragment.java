package com.example.geofencingmajorproject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSettingFragment newInstance(String param1, String param2) {
        UserSettingFragment fragment = new UserSettingFragment();
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


    private static final String TAG = "UserSettingFragment";
    ImageView logoutImg;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    RecyclerView recyclerView;
    ConnectedAdminAdapter adminAdapter;

    ArrayList<ConnectedUsers> adminArrayList;

    ImageView emptyAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_setting, container, false);


        logoutImg = view.findViewById(R.id.img_logout);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.userSettingsRecyclerView);
        emptyAdmin = view.findViewById(R.id.emptyAdmin);
        adminArrayList = new ArrayList<>();

        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "LOGOUT BUTTON CLICKED");
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_layout);

                Button yesbtn, nobtn;
                yesbtn = dialog.findViewById(R.id.yesButton);
                nobtn = dialog.findViewById(R.id.noButton);

                yesbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

                nobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

//        adminArrayList.add(new ConnectedUsers("REQUESTED", "adityanair@gmail.com"));
//        adminArrayList.add(new ConnectedUsers("ACCEPTED", "anuragg@gmail.com"));
//        adapterHandler(adminArrayList);


        firestore.collection("User").document(auth.getCurrentUser().getEmail()).collection("Connected Admin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        adminArrayList.add(new ConnectedUsers(snapshot.getString("Request"), snapshot.getId()));
                    }
//                    adapter.notifyDataSetChanged();
                    if(adminArrayList.isEmpty()){
                        emptyAdmin.setVisibility(View.VISIBLE);
                    }else {
                        emptyAdmin.setVisibility(View.GONE);
                        adapterHandler(adminArrayList);
                        Log.d("SPECIAL", "onCreate: adminArrayList size: " + adminArrayList.get(0).email);
                    }
                }
            }
        });


        return view;
    }

    private void adapterHandler(ArrayList<ConnectedUsers> adminArrayList){
        adminAdapter = new ConnectedAdminAdapter(getContext(), adminArrayList, auth.getCurrentUser().getEmail());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new ConnectedAdminAdapter(getContext(), adminArrayList, auth.getCurrentUser().getEmail()));
    }
}