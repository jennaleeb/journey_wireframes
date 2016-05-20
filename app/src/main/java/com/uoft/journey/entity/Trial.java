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
    private float mMeanStepTime;
    private float mStepSD;
    private float mStepCV;
    private float mGaitSym;
    private float mCadence;
    private float mMeanStrideTime;
    private float mStrideSD;
    private float mStrideCV;
    private List<int[]> mPauseTimes;

    public Trial(int trialId, Date startTime, AccelerometerData data, String username) {
        mTrialId = trialId;
        mStartTime = startTime;
        mTrialData = data;
        mMeanStepTime = 0.0f;
        mStepSD = 0.0f;
        mStepCV = 0.0f;
        mGaitSym = 0.0f;
        mMeanStrideTime = 0.0f;
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

        mGaitSym = parcel.readFloat();
        mCadence = parcel.readFloat();
        mStrideSD = parcel.readFloat();
        mStrideCV = parcel.readFloat();
        mMeanStrideTime = parcel.readFloat();
        mMeanStepTime = parcel.readFloat();
        mStepSD = parcel.readFloat();
        mStepCV = parcel.readFloat();
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

    public float getMeanStepTime() {
        return mMeanStepTime;
    }

    public float getStepSD() {
        return mStepSD;
    }

    public float getStepCV() {
        return mStepCV;
    }

    public float getGaitSym() {
        return mGaitSym;
    }

    public float getMeanStrideTime() {
        return mMeanStrideTime;
    }

    public float getStrideCV() {
        return mStrideCV;
    }

    public float getStrideSD() {
        return mStrideSD;
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

    public float getCadence() {
        // steps per minute
        return (60 * getNumberOfSteps() / (getDuration() / 1000.0f) );
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

    public void setStepAnalysis(float mean, float stepSD, float stepCV, float gaitSym, float cadence, float meanStride, float strideSD, float strideCV) {
        mMeanStepTime = mean;
        mStepSD = stepSD;
        mStepCV = stepCV;
        mGaitSym = gaitSym;
        mCadence = cadence;
        mMeanStrideTime = meanStride;
        mStrideSD = strideSD;
        mStrideCV = strideCV;
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

        dest.writeFloat(mGaitSym);
        dest.writeFloat(mCadence);
        dest.writeFloat(mStrideSD);
        dest.writeFloat(mStrideCV);
        dest.writeFloat(mMeanStrideTime);
        dest.writeFloat(mMeanStepTime);
        dest.writeFloat(mStepSD);
        dest.writeFloat(mStepCV);
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
        if(value <= 5.0f) {
            return Level.GOOD;
        }
        if(value <= 10.0f) {
            return Level.OK;
        }
        return Level.BAD;
    }
}
