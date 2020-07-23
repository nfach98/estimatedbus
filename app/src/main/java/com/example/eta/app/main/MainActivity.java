package com.example.eta.app.main;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.eta.R;
import com.example.eta.app.map.MapActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    Fragment homeFrag = new HomeFragment();
    Fragment accFrag = new AccountFragment();

    BottomNavigationView navView;
    FrameLayout container;
    ImageView fabMap;

    SettingsClient settingsClient;
    int REQUEST_CHECK_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsClient = LocationServices.getSettingsClient(this);
        initView();
    }

    private void initView(){
        Objects.requireNonNull(getSupportActionBar()).hide();

        navView = findViewById(R.id.navMain);
        container = findViewById(R.id.frameMain);
        fabMap = findViewById(R.id.fabMap);

        setFragment(homeFrag);
        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_home :
                    setFragment(homeFrag);
                    return true;
                case R.id.menu_map :
                    return false;
                case R.id.menu_account :
                    setFragment(accFrag);
                    return true;
            }
            return false;
        });

        fabMap.setOnClickListener(view -> checkLocation());
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameMain, fragment);
        transaction.commit();
    }

    private void checkLocation(){
        LocationSettingsRequest.Builder locationRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        LocationSettingsRequest locationRequest = locationRequestBuilder.build();
        settingsClient.checkLocationSettings(locationRequest)
                .addOnSuccessListener(locationSettingsResponse ->
                        startActivity(new Intent(MainActivity.this, MapActivity.class)))
                .addOnFailureListener(e -> {
                   int statusCode = ((ApiException) e).getStatusCode();
                   if(statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                       ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                       try {
                           resolvableApiException.startResolutionForResult(MainActivity.this,
                                   REQUEST_CHECK_SETTINGS);
                       } catch (IntentSender.SendIntentException ex) {
                           ex.printStackTrace();
                       }
                   }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        }
    }
}