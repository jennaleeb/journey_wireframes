package com.uoft.journey.models;

/**
 * Created by jenna on 16-01-19.
 */
public class Patient {

    private String mName;
    private int mDaySinceStart;

    public Patient(String name, int daySinceStart) {
        mName = name;
        mDaySinceStart = daySinceStart;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getDaySinceStart() {
        return mDaySinceStart;
    }

    public void setDaySinceStart (int daySinceStart) {
        mDaySinceStart = daySinceStart;
    }

}
