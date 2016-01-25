package com.patronage.lukaszpiskadlo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_IN_MS = 5000;
    private boolean wasBackPressed;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startMainActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wasBackPressed = false;

        handler.postDelayed(runnable, SPLASH_DURATION_IN_MS);
    }

    @Override
    public void onBackPressed() {
        if(wasBackPressed) {
            super.onBackPressed();
        }
        handler.removeCallbacks(runnable);
        wasBackPressed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
