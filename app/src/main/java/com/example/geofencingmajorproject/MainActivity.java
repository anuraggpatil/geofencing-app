package com.example.geofencingmajorproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();



        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent registration = new Intent(MainActivity.this, SignupActivity.class);
                Intent home = new Intent(MainActivity.this, HomePage.class);
                if(auth.getCurrentUser() != null) {
                    Log.d(TAG, "UID OF LOGGED IN USER: " + auth.getCurrentUser().getUid());

                    firestore.collection("Admin").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    if(documentSnapshot.getString("Email ID").equals(auth.getCurrentUser().getEmail())){
                                        startActivity(home);
                                        break;
                                    }
                                }

                                // if not Admin then check in user collection

                                firestore.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){

                                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                                    Log.d(TAG, "onComplete: hereeeeeeee");
                                                Log.d(TAG, "onComplete: " + documentSnapshot.getString("Email ID"));

                                                if(documentSnapshot.getString("Email ID").equals(auth.getCurrentUser().getEmail()) ){
                                                    Log.d(TAG, "onComplete: hereeeeeeee");

                                                    startActivity(new Intent(MainActivity.this, UserHomePage.class));
                                                    finish();

                                                    break;
                                                }
                                            }
                                        }else{
                                            Log.d(TAG, "onComplete: ERROR GETTING DOCUMENTS");

                                        }
                                    }
                                });

                            }else{
                                Log.d(TAG, "onComplete: ERROR GETTING DOCUMENTS");
                            }
                        }
                    });

//                    startActivity(home);


                }
                else {
                    startActivity(registration);
                                    finish();

                }
            }
        }, 1000);

//        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//        startActivity(intent);
    }
}