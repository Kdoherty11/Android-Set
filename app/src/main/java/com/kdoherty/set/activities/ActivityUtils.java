package com.kdoherty.set.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.kdoherty.set.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.client.Response;

/**
 * Created by kdoherty on 8/20/14.
 */
public class ActivityUtils {

    private ActivityUtils() {
        // Hide constructor
    }

    private static String mUsername;

    public static String getUsername(Context context) {
        if (!Strings.isNullOrEmpty(mUsername)) {
            return mUsername;
        }
        SharedPreferences usernamePrefs = context.getSharedPreferences(Constants.Keys.SPF_USERNAME, Context.MODE_PRIVATE);
        if (usernamePrefs.contains(Constants.Keys.USERNAME)) {
            mUsername = usernamePrefs.getString(Constants.Keys.USERNAME, "Default");
            return mUsername;
        }
        throw new IllegalStateException("Must be logged in");
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean checkOnline(Context context) {
        return checkOnline(context, "Internet connectivity is required");
    }

    public static boolean checkOnline(Context context, String message) {
        boolean isOnline = isOnline(context);
        if (!isOnline) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return isOnline;
    }

    public static int getDifficulty(String option) {
        switch (option) {
            case "Easy":
                return Constants.Cpu.EASY;
            case "Medium":
                return Constants.Cpu.MEDIUM;
            case "Hard":
                return Constants.Cpu.HARD;
            case "Insane":
                return Constants.Cpu.INSANE;
            default:
                throw new IllegalStateException("Illegal option selected " + option);
        }
    }

    public static String getResponseStr(Response result) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
