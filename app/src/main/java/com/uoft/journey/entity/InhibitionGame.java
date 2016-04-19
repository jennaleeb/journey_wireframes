package com.uoft.journey.entity;

import java.util.Date;

/**
 * Created by jenna on 16-04-15.
 */
public class InhibitionGame {
    public static final String  TRIAL_DATA_KEY = "GAME_DATA_KEY";
    private String mUsername;
    private int mGameId;
    private int mTrialId;
    private Date mStartTime;
    private float mOmissionError;
    private float mCommissionError;
    private int mMeanResponseTime;
    private float mOverallAccuracy;

    public InhibitionGame (int gameId, Date startTime, String username) {
        mGameId = gameId;
        mStartTime = startTime;
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public int getGameId() {
        return mGameId;
    }

    public void setGameId(int gameId) {
        mGameId = gameId;
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

    public float getOmissionError() {
        return mOmissionError;
    }

    public void setOmissionError(float omissionError) {
        mOmissionError = omissionError;
    }

    public float getCommissionError() {
        return mCommissionError;
    }

    public void setCommissionError(float commissionError) {
        mCommissionError = commissionError;
    }

    public int getMeanResponseTime() {
        return mMeanResponseTime;
    }

    public void setMeanResponseTime(int meanResponseTime) {
        mMeanResponseTime = meanResponseTime;
    }

    public float getOverallAccuracy() {
        return mOverallAccuracy;
    }

    public void setOverallAccuracy(float accuracy) {
        mOverallAccuracy = accuracy;
    }
}
