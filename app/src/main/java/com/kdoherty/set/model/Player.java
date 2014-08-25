package com.kdoherty.set.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kdoherty on 8/6/14.
 */
public class Player implements Comparable<Player>, Parcelable {

    private String name;
    private int score;

    public Player(String name, String regId, int score) {
        this.name = name;
        this.score = score;
    }

    public Player(Parcel in) {
        readFromParcel(in);
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

    @Override
    public int compareTo(Player player) {
        return player.score - score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(score);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        score = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {

        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }

    };
}
