package com.uoft.journey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Holds accelerometer X,Y,Z values
 * Modified from MyWalk app
 */
public class AccelerometerData implements Parcelable {

    public static final String TAG = AccelerometerData.class.getSimpleName();
    public static final String  ACCELEROMETER_DATA_KEY = "ACCELEROMETER_DATA_KEY";

    private long mStartTimestamp;
    private int[] mElapsedTimestamp;
    private long[] mTimestamp;
    private float[] mAccX;
    private float[] mAccY;
    private float[] mAccZ;
    private int mIndex;

    public AccelerometerData(int size, long startTime) {
        mTimestamp = new long[size];
        mElapsedTimestamp = new int[size];
        mAccX = new float[size];
        mAccY = new float[size];
        mAccZ = new float[size];
        mIndex = 0;
        mStartTimestamp = startTime;
    }

    private AccelerometerData(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        mIndex = parcel.readInt();
        mTimestamp = new long[mIndex];
        mElapsedTimestamp = new int[mIndex];
        mAccX = new float[mIndex];
        mAccY = new float[mIndex];
        mAccZ = new float[mIndex];
        parcel.readLongArray(mTimestamp);
        parcel.readIntArray(mElapsedTimestamp);
        parcel.readFloatArray(mAccX);
        parcel.readFloatArray(mAccY);
        parcel.readFloatArray(mAccZ);
        mStartTimestamp = parcel.readLong();
    }

    public void addData(long timestamp, float x, float y, float z) {
        if(mIndex == 100)
            return;
        if(mStartTimestamp == -1)
            mStartTimestamp = timestamp / 1000000;
        mAccX[mIndex] = x;
        mAccY[mIndex] = y;
        mAccZ[mIndex] = z;
        mTimestamp[mIndex] = timestamp;
        mElapsedTimestamp[mIndex++] = (int) (((timestamp / 1000000) - mStartTimestamp));
    }

    public final float[] getAccelDataX() {
        return mAccX;
    }

    public final float[] getAccelDataY() {
        return mAccY;
    }

    public final float[] getAccelDataZ() {
        return mAccZ;
    }

    public final long[] getTimestamps() {
        return mTimestamp;
    }

    public final int[] getElapsedTimestamps() {
        return mElapsedTimestamp;
    }

    public int getDataCount() {
        return mIndex;
    }

    public long getStartTimestamp() { return mStartTimestamp; }

    public void setStartTimestamp(long timestamp) { mStartTimestamp = timestamp; }

    public boolean isFull() {
        return mIndex == mAccX.length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIndex);
        long[] timestamp = Arrays.copyOfRange(mTimestamp, 0, mIndex);
        int[] elapsed = Arrays.copyOfRange(mElapsedTimestamp, 0, mIndex);
        float[] x = Arrays.copyOfRange(mAccX, 0, mIndex);
        float[] y = Arrays.copyOfRange(mAccY, 0, mIndex);
        float[] z = Arrays.copyOfRange(mAccZ, 0, mIndex);
        dest.writeLongArray(timestamp);
        dest.writeIntArray(elapsed);
        dest.writeFloatArray(x);
        dest.writeFloatArray(y);
        dest.writeFloatArray(z);
        dest.writeLong(mStartTimestamp);
    }

    public static final Parcelable.Creator<AccelerometerData> CREATOR
            = new Parcelable.Creator<AccelerometerData>() {
        public AccelerometerData createFromParcel(Parcel in) {
            return new AccelerometerData(in);
        }

        public AccelerometerData[] newArray(int size) {
            return new AccelerometerData[size];
        }
    };


}
