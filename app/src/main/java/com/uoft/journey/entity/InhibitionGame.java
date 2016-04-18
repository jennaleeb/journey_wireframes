package com.uoft.journey.entity;

import java.util.Date;

/**
 * Created by jenna on 16-04-15.
 */
public class InhibitionGame {
    public static final String  TRIAL_DATA_KEY = "GAME_DATA_KEY";
    private String mUsername;
    private int mTrialId;
    private Date mStartTime;
    private int mMeanResponseTime;
    private int mAccuracy;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public int getTrialId() {
        return mTrialId;
    }

    public void setTrialId(int trialId) {
        mTrialId = trialId;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Date startTime) {
        mStartTime = startTime;
    }

    public int getMeanResponseTime() {
        return mMeanResponseTime;
    }

    public void setMeanResponseTime(int meanResponseTime) {
        mMeanResponseTime = meanResponseTime;
    }

    public int getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(int accuracy) {
        mAccuracy = accuracy;
    }
}
