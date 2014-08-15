package com.kdoherty.model;

/**
 * Created by kdoherty on 8/6/14.
 */
public class Player {

    private String name;
    private int setCount;

    public Player(String name, String regId, int setCount) {
        this.name = name;
        this.setCount = setCount;
    }

    public String getName() {
        return name;
    }

    public int getSetCount() {
        return setCount;
    }

    public String toString() {
        return "Name " + name + " Set Count: " + setCount;
    }
}
