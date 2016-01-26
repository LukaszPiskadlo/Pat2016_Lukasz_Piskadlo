package com.patronage.lukaszpiskadlo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... url) {
        try {
            return downloadJson(url[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

            inputStream = connection.getInputStream();
            return parse(inputStream);
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
}
