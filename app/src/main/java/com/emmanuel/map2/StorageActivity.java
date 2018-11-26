package com.emmanuel.map2;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class StorageActivity extends AppCompatActivity {

    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    String address;
    static final String API_URL_FORWARD = "https://api.what3words.com/v2/forward?";
    static final String API_URL_REVERSE = "https://api.what3words.com/v2/reverse?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StorageActivity.RetrievePositionTask().execute();
            }
        });
    }

    class RetrievePositionTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = new StrictHostnameVerifier();

                return hv.verify("example.com", session);
            }
        };

        protected String doInBackground(Void... urls) {
            // String address = (String) findViewById(R.id.str).getText().toString();
            // Do some validation here

            try {
                final EditText edit =  (EditText) findViewById(R.id.str);
                    address = edit.getText().toString();
                    URL url = new URL(API_URL_FORWARD + "addr=" + address + "&key=" + API_KEY);
                    Log.d("here", "error*");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    //urlConnection.setHostnameVerifier(hostnameVerifier);
                    int length = urlConnection.getContentLength();
                    InputStream in = urlConnection.getInputStream();

                    try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            TextView responseView = (TextView) findViewById(R.id.responseView);
            // TODO: check this.exception
            // TODO: do something with the feed

            try {

                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                String geometry = object.getString("geometry");
                responseView.setText(geometry);

              //  String geometry = object.getJSONObject("geometry");
              //  Log.i("geometry", geometry);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveWordsTask extends AsyncTask<Void, Void, String> {

        private Exception exception;



        protected String doInBackground(Void... urls) {
            final EditText edit =  (EditText) findViewById(R.id.str);
            address = edit.getText().toString();

            try {
                URL url = new URL(API_URL_REVERSE + "addr=" + address + "&key=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);
            responseView.setText(response);
        }
    }






}

