package com.kdoherty.set.activities;

import com.kdoherty.set.Constants;

/**
 * Created by kdoherty on 8/20/14.
 */
public class ActivityUtils {

    private ActivityUtils() {
        // Hide constructor
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
