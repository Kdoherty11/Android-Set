package com.kdoherty.set.model;

import com.google.common.base.Objects;

/**
 * Created by kdoherty on 8/29/14.
 */
public class LeaderboardEntry {

    private String name;
    private long score;
    private String mode;
    private int key;

    public String getName() {
        return name;
    }

    public long getScore() {
        return score;
    }

    public String getMode() {
        return mode;
    }

    public int getKey() {
        return key;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("Name", name)
                .add("Score", score)
                .add("mode", mode)
                .add("Key", key)
                .toString();
    }
}
