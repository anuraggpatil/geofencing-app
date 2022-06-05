package com.example.geofencingmajorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private Button adminButton, userButton, adminSignup, userSignup, haveAccount;
    LinearLayout chooseProfile, registrationPage;

    EditText mUsername, mEmail, mPassword, mPhone;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String passwordVer = "^" +
            "(?=.*[a-zA-Z])" +       // any letter
            "(?=.*[@#$%^&+=])" +     // at least 1 special character
            "(?=\\S+$)" +            // no white spaces
            ".{6,}" +                // at least 4 characters
            "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        adminButton = findViewById(R.id.parent_button);
        userButton = findViewById(R.id.child_button);
        chooseProfile = findViewById(R.id.chooseProfile);
        registrationPage = findViewById(R.id.resgistrationPage);
        adminSignup = findViewById(R.id.SignUpParent);
        userSignup = findViewById(R.id.SignUpChild);
        haveAccount = findViewById(R.id.LogIn);

        mUsername = findViewById(R.id.userName);
        mEmail = findViewById(R.id.emailID);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phoneNumber);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select Date Of Birth");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();



        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrationPage.setVisibility(View.VISIBLE);
                chooseProfile.setVisibility(View.GONE);
                adminSignup.setVisibility(View.VISIBLE);
                userSignup.setVisibility(View.GONE);
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrationPage.setVisibility(View.VISIBLE);
                chooseProfile.setVisibility(View.GONE);
                adminSignup.setVisibility(View.GONE);
                userSignup.setVisibility(View.VISIBLE);
            }
        });

        adminSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidInput()==true) {


                    final String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    final String username = mUsername.getText().toString();
                    final String phone    = "+91" + mPhone.getText().toString().trim();
                    final String default_URI = "https://firebasestorage.googleapis.com/v0/b/fir-project-2b9f4.appspot.com/o/displaypicture.png?alt=media&token=007775cd-0561-46a9-b812-e3b8d0573346";
                    final String userID;
                    final int geofenceRadius = 200;

                    fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(SignupActivity.this, "User Created by their Email,", Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onSuccess: " + fAuth.getCurrentUser().getEmail());

                            DocumentReference documentReference = fStore.collection("Admin").document(fAuth.getCurrentUser().getEmail());
                            Map<String, Object> admin = new HashMap<>();
                            admin.put("Name", username);
                            admin.put("Email ID", email);
                            admin.put("Phone No.", phone);
                            admin.put("Geofence Radius", geofenceRadius);

                            documentReference.set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + username);
                                    Intent intent = new Intent(SignupActivity.this, HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "Error! Check your Internet Connection.", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        userSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidInput()==true){

                    final String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    final String username = mUsername.getText().toString();
                    final String phone    = "+91" + mPhone.getText().toString().trim();
                    final String default_URI = "https://firebasestorage.googleapis.com/v0/b/fir-project-2b9f4.appspot.com/o/displaypicture.png?alt=media&token=007775cd-0561-46a9-b812-e3b8d0573346";
                    final String userID;

                    fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(SignupActivity.this, "User Created By Their Email.", Toast.LENGTH_SHORT).show();

                            // Move to verify Phone Number and store user details after the Phone verification
                            DocumentReference documentReference = fStore.collection("User").document(fAuth.getCurrentUser().getEmail());
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", username);
                            user.put("Email ID", email);
                            user.put("Phone No.", phone);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + username);
                                    Intent intent = new Intent(SignupActivity.this, UserHomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "Error! Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
            }
        });
    }

    public boolean checkValidInput(){

        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String username = mUsername.getText().toString();
        final String phone    = "+91" + mPhone.getText().toString().trim();
        final String default_URI = "https://firebasestorage.googleapis.com/v0/b/fir-project-2b9f4.appspot.com/o/displaypicture.png?alt=media&token=007775cd-0561-46a9-b812-e3b8d0573346";


        if(username.isEmpty()){
            mUsername.setError("Field Cannot be Empty.");
            return false;
        }
        if(email.isEmpty()){
            mEmail.setError("Field Cannot be Empty.");
            return false;
        }
        if(password.isEmpty()){
            mPassword.setError("Field Cannot be Empty.");
            return false;
        }
        if(phone.isEmpty()){
            mPhone.setError("Field Cannot be Empty.");
            return false;
        }
        if(phone.length() < 10){
            mPhone.setError("Enter a 10-Digit Phone Number.");
            return false;
        }
        if(!email.matches(emailPattern)){
            mEmail.setError("Invalid Email Address.");
            return false;
        }
        if(!password.matches(passwordVer)){
            mPassword.setError("Password is Too Weak : \nMust Have 6 Characters\n1 Special Character\nNo Blank Spaces");
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        chooseProfile.setVisibility(View.VISIBLE);
        registrationPage.setVisibility(View.GONE);
        mUsername.setText("");
        mEmail.setText("");
        mPhone.setText("");
        mPassword.setText("");

    }
    }