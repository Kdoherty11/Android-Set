package com.kdoherty.set.model;

/**
 * Created by kdoherty on 8/6/14.
 */
public class Player {

    private String name;
    private int score;

    public Player(String name, String regId, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getSetCount() {
        return score;
    }

    public String toString() {
        return "Name " + name + " Set Count: " + score;
    }
}
