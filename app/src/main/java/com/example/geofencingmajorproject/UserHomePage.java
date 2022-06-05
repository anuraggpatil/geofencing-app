package com.example.geofencingmajorproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHomePage extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
//
        BottomNavigationView bottomNavigationView = findViewById(R.id.userBottomNavigationView);
        bottomNavigationView.setBackground(null);

        getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer,new UserHomeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {

        switch(item.getItemId())
        {
            case R.id.userSettings:
                replaceFragment(new UserSettingFragment());
                break;
            case R.id.userHome:
                replaceFragment(new UserHomeFragment());
                break;

        }
        return true;
    };

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, fragment).commit();
    }
}