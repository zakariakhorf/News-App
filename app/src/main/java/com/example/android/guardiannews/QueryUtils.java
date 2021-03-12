package com.example.android.guardiannews;

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


public class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Constructor
    private QueryUtils() {
    }

    // Methods
    public static List<NewsItem> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeaturesFromJson(jsonResponse);
    }

    private static URL createUrl(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;

        HttpURLConnection httpUrlConnection = null;
        InputStream inputStream = null;
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setReadTimeout(10000 /* milliseconds */);
            httpUrlConnection.setConnectTimeout(15000 /* milliseconds */);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            if (httpUrlConnection.getResponseCode() == 200) {
                inputStream = httpUrlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpUrlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (httpUrlConnection != null)
                httpUrlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
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

    private static List<NewsItem> extractFeaturesFromJson(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.length() == 0)
            return null;

        ArrayList<NewsItem> newsList = new ArrayList<>();
        try {
            JSONObject baseResponse = new JSONObject(jsonResponse);
            JSONObject responseObj = baseResponse.getJSONObject("response");
            JSONArray resultsArray = responseObj.getJSONArray("results");

            if (resultsArray.length() > 0) {
                for (int i = 0; i < resultsArray.length(); ++i) {
                    JSONObject obj = resultsArray.getJSONObject(i);

                    String authorName = null;
                    try {
                        authorName = obj.getJSONArray("tags").
                                getJSONObject(0).
                                getString("webTitle");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Problem getting author name of article", e);
                    }

                    newsList.add(new NewsItem(obj.getString("webTitle"),
                            obj.getString("sectionName"),
                            authorName != null ? authorName : "Not available",
                            obj.getString("webPublicationDate"),
                            obj.getString("webUrl")));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }

        return newsList;
    }
}
