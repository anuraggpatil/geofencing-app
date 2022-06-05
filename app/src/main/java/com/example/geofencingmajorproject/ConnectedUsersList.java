package com.example.geofencingmajorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ConnectedUsersList extends AppCompatActivity {

    private static final String TAG = "ConnectedUsersList";

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    RecyclerView userRecyclerView;

    ArrayList<ConnectedUsers> usersArrayList;
    ConnectedUsersAdapter adapter;

    Button userAddButton;
    LinearLayout noUser;
    FloatingActionButton addbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_users_list);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        usersArrayList = new ArrayList<>();
        ArrayList<ConnectedUsers> temp = new ArrayList<>();
        userRecyclerView = findViewById(R.id.userRecyclerView);
        noUser = findViewById(R.id.emptyUser);
        userAddButton = findViewById(R.id.userAddButton);
        addbutton = findViewById(R.id.connectedUsersAddButton);

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConnectedUsersList.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        userAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConnectedUsersList.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        firestore.collection("Admin").document(firebaseAuth.getCurrentUser().getEmail()).collection("Users Connected").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        usersArrayList.add(new ConnectedUsers(snapshot.getString("Request"), snapshot.getId()));
                    }
//                    adapter.notifyDataSetChanged();
                    Log.d("SPECIAL", "onCreate: users array list size " + usersArrayList.size());

                    if(usersArrayList.isEmpty()){
                        noUser.setVisibility(View.VISIBLE);
                        addbutton.setVisibility(View.GONE);

                    }else {
                        noUser.setVisibility(View.GONE);
                        adapterHandler(usersArrayList);
                        addbutton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    private void adapterHandler(ArrayList<ConnectedUsers> usersArrayList){
        adapter = new ConnectedUsersAdapter(this, usersArrayList, firebaseAuth.getCurrentUser().getEmail());
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        userRecyclerView.setAdapter(new ConnectedUsersAdapter(this, usersArrayList, firebaseAuth.getCurrentUser().getEmail()));
    }
}