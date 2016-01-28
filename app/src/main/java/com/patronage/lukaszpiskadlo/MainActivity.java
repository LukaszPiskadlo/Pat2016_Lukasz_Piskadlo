package com.patronage.lukaszpiskadlo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String BASE_SERVER_URL = "http://10.0.2.2:8080/";
    final static String FILE_NAME = "page_";
    final static String FILE_EXTENSION = ".json";
    private int page;
    private boolean isLastPage;
    private List<Item> imageList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageList = new ArrayList<>();
        page = 0;
        isLastPage = false;
        requestJsonData();
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

    private void requestJsonData() {
        if(isOnline() && !isLastPage) {
            new DownloadTask().execute(BASE_SERVER_URL + FILE_NAME + page + FILE_EXTENSION);
            page++;
        }
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

    private class DownloadTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... url) {
            try {
                return downloadJson(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            if(jsonArray != null) {
                jsonToItem(jsonArray);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isLastPage = true;
            progressBar.setVisibility(View.INVISIBLE);
        }

        /**
         * Downloads JSON file content from HTTP server
         * @param url server address
         * @return JSONArray of downloaded data
         * @throws IOException
         */
        private JSONArray downloadJson(String url) throws IOException {
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            try {
                // setup connection
                URL urlObj = new URL(url);
                connection = (HttpURLConnection) urlObj.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    return parse(inputStream);
                } else {
                    cancel(true);
                    return null;
                }
            } finally {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(connection != null) {
                    connection.disconnect();
                }
            }
        }

        /**
         * Parsing InputStream into JSONArray
         * @throws IOException
         */
        private JSONArray parse(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder str = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                str.append(line);
            }

            try {
                JSONObject jObj = new JSONObject(str.toString());
                return jObj.getJSONArray("array");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void jsonToItem(JSONArray jArr) {
            try {
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    String title = jObj.getString("title");
                    String desc = jObj.getString("desc");
                    String url = jObj.getString("url");

                    imageList.add(new Item(title, desc, url));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
