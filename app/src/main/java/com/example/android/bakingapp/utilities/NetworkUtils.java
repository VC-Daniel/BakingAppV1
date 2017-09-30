package com.example.android.bakingapp.utilities;

/*
 * This class is a modified version of the NetworkUtils class that was provided in the Sunshine app.
 */

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import fr.arnaudguyon.logfilter.Log;

/**
 * These utilities will be used to communicate with recipe data api.
 * <p>
 * The logic for setting a timeout on the url connection was inspired by:
 * https://eventuallyconsistent.net/2011/08/02/working-with-urlconnection-and-timeouts/
 */
public final class NetworkUtils {
    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // The path to the recipe data
    private static final String RECIPE_DATA_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    /**
     * Builds the URL used to get the recipe data from the server
     *
     * @return The URL to use to query the recipe data server.
     */
    private static URL buildUrl() {
        // build the uri with the base path
        Uri builtUri = Uri.parse(RECIPE_DATA_BASE_URL);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error occurred while building the recipe data URL:" + e.getMessage());
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        // Return the path to use to retrieve the recipe data
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl() throws IOException {
        URL url = buildUrl();
        URLConnection connection = url.openConnection();

        String data = "";

        // set the connection timeout and the read timeout
        int connectionTimeout = 5000;
        connection.setConnectTimeout(connectionTimeout);
        int readTimeout = 10000;
        connection.setReadTimeout(readTimeout);
        BufferedReader in = null;
        try {
            // get a stream to read data from
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                data = scanner.next();
            } else {
                data = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error encountered while getting the response from the baking api: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return data;
    }
}