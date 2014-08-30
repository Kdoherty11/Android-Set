package com.kdoherty.set.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Strings;
import com.kdoherty.set.Constants;

/**
 * Created by kdoherty on 8/20/14.
 */
public class ActivityUtils {

    private ActivityUtils() {
        // Hide constructor
    }

    private static String mUsername;

    public static String getUsername(Activity activity) {
        if (!Strings.isNullOrEmpty(mUsername)) {
            return mUsername;
        }
        SharedPreferences usernamePrefs = activity.getSharedPreferences(Constants.Keys.SPF_USERNAME, Context.MODE_PRIVATE);
        if (usernamePrefs.contains(Constants.Keys.USERNAME)) {
            mUsername = usernamePrefs.getString(Constants.Keys.USERNAME, "Default");
            return mUsername;
        }
        throw new IllegalStateException("Must be logged in");
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
}
