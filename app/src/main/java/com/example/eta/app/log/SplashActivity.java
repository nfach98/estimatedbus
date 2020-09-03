package com.example.eta.app.log;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.eta.R;
import com.example.eta.app.main.MainActivity;
import com.example.eta.util.UserPref;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

/** activity untuk menampilkan splash screen
 *  muncul paling awal ketika membuka aplikasi
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();

        new Handler().postDelayed(() -> {
            if(UserPref.getUser(SplashActivity.this).getEmail().equals(""))
                startActivity(new Intent(SplashActivity.this, MasukActivity.class));
            else startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 4000);
    }
}