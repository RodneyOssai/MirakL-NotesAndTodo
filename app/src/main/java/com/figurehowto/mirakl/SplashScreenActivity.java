package com.figurehowto.mirakl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Intent homeActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(homeActivity);
            finish();
        },SPLASH_TIME_OUT);
    }
}
