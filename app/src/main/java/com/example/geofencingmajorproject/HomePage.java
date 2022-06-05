package com.example.geofencingmajorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.geofencingmajorproject.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomePage extends AppCompatActivity {

    private static final String TAG = "HomePage";

    ActivityMainBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);

        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer,new HomeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        FloatingActionButton addButton=findViewById(R.id.addButton);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        addButton.setOnClickListener(v->{
            Intent intent = new Intent(HomePage.this, AddUserActivity.class);
            startActivity(intent);
        });

    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {

        switch(item.getItemId())
        {
            case R.id.home:
                replaceFragment(new HomeFragment());
                break;
            case R.id.settings:
                replaceFragment(new SettingsFragment());
                break;

        }
        return true;
    };

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, fragment).commit();

    }
}