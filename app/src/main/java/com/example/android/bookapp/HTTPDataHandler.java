package com.example.android.bookapp;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPDataHandler {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = HTTPDataHandler.class.getSimpleName();
    private static String inputStream = null;

    public HTTPDataHandler() {
    }

    public String GetHTTPData(String urlString) {

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            // Check the connection status
            if (urlConnection.getResponseCode() == 200) {
                // if response code = 200 ok
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Read the BufferedInputStream
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }
                inputStream = sb.toString();

                // Disconnect the HttpURLConnection
                urlConnection.disconnect();
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        // Return the data from specified url
        return inputStream;
    }
}
