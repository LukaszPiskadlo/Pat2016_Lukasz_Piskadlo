package com.patronage.lukaszpiskadlo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_IN_MS = 5000;
    private Handler handler;
    private Runnable runnable;
    private boolean wasBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wasBackPressed = false;

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity();
            }
        };
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

    private void startActivity() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        boolean isLoggedIn = settings.getBoolean(getString(R.string.key_logged_in), false);

        Intent intent;
        if(isLoggedIn) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
