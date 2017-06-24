package com.example.android.guardiannewsapp;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtilities {
    private static final String LOG_TAG = QueryUtilities.class.getSimpleName();

    //Constructor
    private QueryUtilities() {
    }

    public static ArrayList<News> getResultsFromJson(String newsJSON) {
        // in case json string is empty or null
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        ArrayList<News> news = new ArrayList<>();
        try {
            JSONObject reader = new JSONObject(newsJSON);
            JSONObject response = reader.getJSONObject("response");
            JSONArray newsArray = response.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject newsObject = newsArray.getJSONObject(i);
                String section = newsObject.getString("sectionName");
                String title = newsObject.getString("webTitle");
                String type = newsObject.getString("type");
                String date = newsObject.getString("webPublicationDate");
                date = date.replace("T", " T: ");
                date = date.replace("Z", "");
                String url = newsObject.getString("webUrl");
                News newsResult = new News(section, title, type, date, url);
                news.add(newsResult);
            }
        } catch (JSONException e) {
            Log.e("QueryUtilities", "Problem parsing the news JSON results", e);
        }
        return news;
    }

    public static List<News> fetchNewsData(String requestUrl) {
        try {
            Thread.sleep(2000 /* millisecond*/);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Url object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from JSON response and create an object
        return getResultsFromJson(jsonResponse);
    }

    private static URL createUrl(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // in case url is null
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }
}
