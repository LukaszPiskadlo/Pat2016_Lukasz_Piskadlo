package com.patronage.lukaszpiskadlo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    final static String BASE_SERVER_URL = "http://10.0.3.2:8080/";
    final static String FILE_NAME = "page_";
    final static String FILE_EXTENSION = ".json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isOnline()) {
            new DownloadTask().execute(BASE_SERVER_URL + FILE_NAME + 0 + FILE_EXTENSION);
        }
    }

    /**
     * Checks if device is connected to network
     * @return true if device is connected to network
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void logout() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(getString(R.string.key_logged_in), false);
        editor.commit();

        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
