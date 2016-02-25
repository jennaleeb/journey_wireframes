package com.uoft.journey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * A trial
 */
public class Trial implements Parcelable {
    public static final String  TRIAL_DATA_KEY = "TRIAL_DATA_KEY";
    private int mTrialId;
    private Date mStartTime;
    private AccelerometerData mTrialData;
    private int[] mStepTimes;

    public Trial(int trialId, Date startTime, AccelerometerData data) {
        mTrialId = trialId;
        mStartTime = startTime;
        mTrialData = data;
    }

    private Trial(Parcel parcel) {
        mTrialId = parcel.readInt();
        mStartTime = (Date)parcel.readSerializable();
        try {
            mTrialData = parcel.readParcelable(AccelerometerData.class.getClassLoader());
        }
        catch (Exception e) {
            mTrialData = null;
        }

        int steps = parcel.readInt();
        if(steps > 0) {
            mStepTimes = new int[steps];
            parcel.readIntArray(mStepTimes);
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

    public void setTrialData(AccelerometerData data) {
        mTrialData = data;
    }

    public void setStepTimes(int[] steps) {
        mStepTimes = steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mTrialId);
        dest.writeSerializable(mStartTime);
        dest.writeParcelable(mTrialData, flags);

        if(mStepTimes != null) {
            dest.writeInt(mStepTimes.length);
            dest.writeIntArray(mStepTimes);
        }
        else {
            dest.writeInt(-1);
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
}
