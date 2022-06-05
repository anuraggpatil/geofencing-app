package com.example.geofencingmajorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {

    EditText mEmail;
    FirebaseAuth fAuth;
    Button addUser;
    FirebaseFirestore firestore;
//    UserRecord userRecord;
    private static final String TAG = "AddUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        mEmail = findViewById(R.id.addEmail);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        addUser = findViewById(R.id.addUserBtn);

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                DocumentReference userdoc = firestore.collection("User").document(email);

                userdoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                DocumentReference documentReference = firestore.collection("Admin").document(fAuth.getCurrentUser().getEmail()).collection("Users Connected").document(email);
                                Map<String, Object> userConnected = new HashMap<>();
                                userConnected.put("Email ID", email);
                                userConnected.put("Request", "REQUESTED"); //REQUESTED, ACCEPTED, DECLINED
                                documentReference.set(userConnected).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mEmail.setText("");
                                        startActivity(new Intent(AddUserActivity.this, HomePage.class));
                                        Toast.makeText(AddUserActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddUserActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                mEmail.setText("");
                                Toast.makeText(AddUserActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                Map<String, Object> adminConnected = new HashMap<>();
                adminConnected.put("Email ID", fAuth.getCurrentUser().getEmail());
                adminConnected.put("Request", "REQUESTED");
                userdoc.collection("Connected Admin").document(fAuth.getCurrentUser().getEmail()).set(adminConnected);
            }
        });
    }
}