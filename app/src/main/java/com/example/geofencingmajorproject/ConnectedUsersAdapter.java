package com.example.geofencingmajorproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectedUsersAdapter extends RecyclerView.Adapter<ConnectedUsersAdapter.ConnectedUsersViewHolder>{
    Context context;
    ArrayList<ConnectedUsers> connectedUsersArrayList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String email;

    public ConnectedUsersAdapter(Context context, ArrayList<ConnectedUsers> connectedUsersArrayList, String email){
        this.context= context;
        this.connectedUsersArrayList = connectedUsersArrayList;
        this.email = email;
    }


    @NonNull
    @Override
    public ConnectedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.connected_users_list_item_layout, parent, false);

        return new ConnectedUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedUsersAdapter.ConnectedUsersViewHolder holder, int position) {
        ConnectedUsers connectedUsers = connectedUsersArrayList.get(position);

        ConnectedUsersViewHolder connectedUsersViewHolder = (ConnectedUsersViewHolder) holder;
        connectedUsersViewHolder.requestType.setText(connectedUsers.request);
        connectedUsersViewHolder.userListEmail.setText(connectedUsers.email);
        firestore.collection("User").document(connectedUsers.email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    connectedUsersViewHolder.userListName.setText(task.getResult().getString("Name"));
                }
            }
        });
        connectedUsersViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Admin").document(firebaseAuth.getCurrentUser().getEmail()).collection("Users Connected").document(connectedUsers.email).delete();
                firestore.collection("User").document(connectedUsers.email).collection("Connected Admin").document(firebaseAuth.getCurrentUser().getEmail()).delete();
            }
        });

    }

    @Override
    public int getItemCount() {
        return connectedUsersArrayList.size();
    }

    public class ConnectedUsersViewHolder extends RecyclerView.ViewHolder {
        TextView userListName;
        TextView userListEmail;

        TextView requestType;
        ImageView delete;

        public ConnectedUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userListName = itemView.findViewById(R.id.userListName);
            userListEmail = itemView.findViewById(R.id.userListEmail);
            requestType = itemView.findViewById(R.id.requestType);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
