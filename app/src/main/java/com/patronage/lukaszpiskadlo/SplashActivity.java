package com.patronage.lukaszpiskadlo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {

    private static final int TIME = 5000; // how long splash screen will be displayed in ms
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
                startMainActivity();
            }
        };
        handler.postDelayed(runnable, TIME);
    }

    @Override
    public void onBackPressed() {
        if(wasBackPressed) {
            super.onBackPressed();
        }

        handler.removeCallbacks(runnable);
        wasBackPressed = true;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
