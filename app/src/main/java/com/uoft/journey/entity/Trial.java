package com.uoft.journey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A trial
 */
public class Trial implements Parcelable {
    public static final String  TRIAL_DATA_KEY = "TRIAL_DATA_KEY";
    private String mUsername;
    private int mTrialId;
    private Date mStartTime;
    private AccelerometerData mTrialData;
    private int[] mStepTimes;
    private float mMeanStrideTime;
    private float mStandardDev;
    private float mCoeffOfVar;
    private float mGaitSym;
    private List<int[]> mPauseTimes;

    public Trial(int trialId, Date startTime, AccelerometerData data, String username) {
        mTrialId = trialId;
        mStartTime = startTime;
        mTrialData = data;
        mMeanStrideTime = 0.0f;
        mStandardDev = 0.0f;
        mCoeffOfVar = 0.0f;
        mPauseTimes = new ArrayList<>();
    }

    private Trial(Parcel parcel) {
        mTrialId = parcel.readInt();
        mStartTime = (Date)parcel.readSerializable();
        if(parcel.readByte() == 1) {
            mTrialData = parcel.readParcelable(AccelerometerData.class.getClassLoader());
        }

        int steps = parcel.readInt();
        if(steps > 0) {
            mStepTimes = new int[steps];
            parcel.readIntArray(mStepTimes);
        }

        mMeanStrideTime = parcel.readFloat();
        mStandardDev = parcel.readFloat();
        mCoeffOfVar = parcel.readFloat();
        mUsername = parcel.readString();

        try {
            parcel.readList(mPauseTimes, int[].class.getClassLoader());
        }
        catch (Exception e) {
            mPauseTimes = new ArrayList<>();
        }
    }

    public int getTrialId() {
        return mTrialId;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public AccelerometerData getTrialData() {
        return mTrialData;
    }

    public int[] getStepTimes() {
        return mStepTimes;
    }

    public List<int[]> getPauseTimes() {
        return mPauseTimes;
    }

    public float getMeanStrideTime() {
        return mMeanStrideTime;
    }

    public float getStandardDev() {
        return mStandardDev;
    }

    public float getCoeffOfVar() {
        return mCoeffOfVar;
    }

    public float getGaitSym() {
        return mGaitSym;
    }

    public int getDuration() {
        if(mStepTimes != null && mStepTimes.length > 1)
            return mStepTimes[mStepTimes.length - 1];

        return 0;
    }

    public int getNumberOfSteps() {
        if(mStepTimes != null)
            return mStepTimes.length;

        return 0;
    }

    public void setTrialData(AccelerometerData data) {
        mTrialData = data;
    }

    public void setStepTimes(int[] steps) {
        mStepTimes = steps;
    }

    public void setPauseTimes(List<int[]> pauses) {
        mPauseTimes = pauses;
    }

    public void setStepAnalysis(float mean, float standardDev, float coeffOfVar, float gaitSym) {
        mMeanStrideTime = mean;
        mStandardDev = standardDev;
        mCoeffOfVar = coeffOfVar;
        mGaitSym = gaitSym;
    }

    public void setUsername(String user){
        mUsername = user;
    }
    public String getUsername(){
        return mUsername;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTrialId);
        dest.writeSerializable(mStartTime);

        if(mTrialData != null) {
            dest.writeByte((byte)1);
            dest.writeParcelable(mTrialData, flags);
        }
        else {
            dest.writeByte((byte)0);
        }

        if(mStepTimes != null) {
            dest.writeInt(mStepTimes.length);
            dest.writeIntArray(mStepTimes);
        }
        else {
            dest.writeInt(-1);
        }

        dest.writeFloat(mMeanStrideTime);
        dest.writeFloat(mStandardDev);
        dest.writeFloat(mCoeffOfVar);
        dest.writeString(mUsername);

        if(mPauseTimes != null) {
            dest.writeList(mPauseTimes);
        }
    }

    public static final Parcelable.Creator<Trial> CREATOR
            = new Parcelable.Creator<Trial>() {
        public Trial createFromParcel(Parcel in) {
            return new Trial(in);
        }

        public Trial[] newArray(int size) {
            return new Trial[size];
        }
    };

    public enum Level { GOOD, OK, BAD }

    public static Level getLevel(float value) {
        if(value <= 4.0f) {
            return Level.GOOD;
        }
        if(value <= 8.0f) {
            return Level.OK;
        }
        return Level.BAD;
    }
}
