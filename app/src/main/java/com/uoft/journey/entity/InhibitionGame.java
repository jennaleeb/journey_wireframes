package com.uoft.journey.entity;

import java.util.Date;

/**
 * Created by jenna on 16-04-15.
 */
public class InhibitionGame {
    private String mUsername;
    private int mGameId;
    private int mTrialId;
    private Date mStartTime;
    private int mHitCount;
    private int mMissCount;
    private int mFalseAlarmCount;
    private int mCorrectNegCount;
    private float mOmissionError;
    private float mCommissionError;
    private int mMeanResponseTime;
    private float mSDResponseTime;
    private int mMeanFalseAlarmRT;
    private float mSDFalseAlarmRT;
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

    public int getMissCount() {
        return mMissCount;
    }

    public void setMissCount(int missCount) {
        mMissCount = missCount;
    }

    public int getHitCount() {
        return mHitCount;
    }

    public void setHitCount(int hitCount) {
        mHitCount = hitCount;
    }

    public int getCorrectNegCount() {
        return mCorrectNegCount;
    }

    public void setCorrectNegCount(int correctNegCount) {
        mCorrectNegCount = correctNegCount;
    }

    public int getFalseAlarmCount() {
        return mFalseAlarmCount;
    }

    public void setFalseAlarmCount(int falseAlarmCount) {
        mFalseAlarmCount = falseAlarmCount;
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

    public float getSDResponseTime() {
        return mSDResponseTime;
    }

    public void setSDResponseTime(float SDResponseTime) {
        mSDResponseTime = SDResponseTime;
    }

    public int getMeanFalseAlarmRT() {
        return mMeanFalseAlarmRT;
    }

    public void setMeanFalseAlarmRT(int meanFalseAlarmRT) {
        mMeanFalseAlarmRT = meanFalseAlarmRT;
    }

    public float getSDFalseAlarmRT() {
        return mSDFalseAlarmRT;
    }

    public void setSDFalseAlarmRT(float SDFalseAlarmRT) {
        mSDFalseAlarmRT = SDFalseAlarmRT;
    }

    public float getOverallAccuracy() {
        return mOverallAccuracy;
    }

    public void setOverallAccuracy(float accuracy) {
        mOverallAccuracy = accuracy;
    }
}
