package com.example.geofencingmajorproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectedAdminAdapter extends RecyclerView.Adapter<ConnectedAdminAdapter.ConnectedAdminViewHolder> {
    Context context;
    ArrayList<ConnectedUsers> connectedAdminArrayList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String email;

    public ConnectedAdminAdapter(Context context, ArrayList<ConnectedUsers> connectedAdminArrayList, String email){
        this.context = context;
        this.connectedAdminArrayList = connectedAdminArrayList;
        Log.d("SPECIAL", "ConnectedAdminAdapter: " + connectedAdminArrayList.size());
        this.email = email;
    }

    @NonNull
    @Override
    public ConnectedAdminAdapter.ConnectedAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.connected_admin_list_item_layout, parent, false);

        return new ConnectedAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedAdminAdapter.ConnectedAdminViewHolder holder, int position) {
        ConnectedUsers connectedAdmin = connectedAdminArrayList.get(position);

        ConnectedAdminViewHolder connectedAdminViewHolder = (ConnectedAdminViewHolder) holder;
        connectedAdminViewHolder.adminListEmail.setText(connectedAdmin.email);
        firestore.collection("Admin").document(connectedAdmin.email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    connectedAdminViewHolder.adminListName.setText(task.getResult().getString("Name"));
                }
            }
        });

        if (connectedAdmin.request.equals("REQUESTED")){
            connectedAdminViewHolder.acceptOrDecline.setVisibility(View.VISIBLE);
            connectedAdminViewHolder.adminDelete.setVisibility(View.GONE);

        }
        else{
            connectedAdminViewHolder.acceptOrDecline.setVisibility(View.GONE);
            connectedAdminViewHolder.adminDelete.setVisibility(View.VISIBLE);
        }

        connectedAdminViewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> usersConnected = new HashMap<>();
                usersConnected.put("Request", "ACCEPTED");
                firestore.collection("Admin").document(connectedAdmin.email).collection("Users Connected").document(firebaseAuth.getCurrentUser().getEmail()).update(usersConnected);
                firestore.collection("User").document(email).collection("Connected Admin").document(connectedAdmin.email).update(usersConnected);
                connectedAdminViewHolder.adminDelete.setVisibility(View.VISIBLE);
                connectedAdminViewHolder.acceptOrDecline.setVisibility(View.GONE);
            }
        });

        connectedAdminViewHolder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> usersConnected = new HashMap<>();
                usersConnected.put("Request", "DECLINED");
                firestore.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).collection("Connected Admin").document(connectedAdmin.email).delete();
                firestore.collection("Admin").document(connectedAdmin.email).collection("Users Connected").document(firebaseAuth.getCurrentUser().getEmail()).update(usersConnected);
            }
        });

        connectedAdminViewHolder.adminDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> usersConnected = new HashMap<>();
                usersConnected.put("Request", "DECLINED");
                firestore.collection("User").document(firebaseAuth.getCurrentUser().getEmail()).collection("Connected Admin").document(connectedAdmin.email).delete();
                firestore.collection("Admin").document(connectedAdmin.email).collection("Users Connected").document(firebaseAuth.getCurrentUser().getEmail()).update(usersConnected);
            }
        });

    }

    @Override
    public int getItemCount() {
        return connectedAdminArrayList.size();
    }

    public class ConnectedAdminViewHolder extends RecyclerView.ViewHolder {
        TextView adminListName;
        TextView adminListEmail;

        ImageView adminDelete;
        ImageButton accept;
        Button decline;

        LinearLayout acceptOrDecline;
        public ConnectedAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            adminListName = itemView.findViewById(R.id.adminListName);
            adminListEmail = itemView.findViewById(R.id.adminListEmail);
            adminDelete = itemView.findViewById(R.id.adminDelete);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);
            acceptOrDecline = itemView.findViewById(R.id.acceptOrDecline);
        }
    }
}
