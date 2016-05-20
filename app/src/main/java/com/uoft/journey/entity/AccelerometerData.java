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
    private float[] mProcessedX;
    private float[] mProcessedY;
    private float[] mProcessedZ;
    private int[] mTrialDataIds;
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

    public AccelerometerData(int[] trailDataIds, int[] elapsedVals, float[] xVals, float[] yVals, float[] zVals) {
        mTrialDataIds = trailDataIds;
        mAccX = xVals;
        mAccY = yVals;
        mAccZ = zVals;
        mElapsedTimestamp = elapsedVals;
//        mTimestamp = timeVals;
//        mStartTimestamp = timeVals[0];
        mIndex = elapsedVals.length;
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

        if(parcel.readByte() == 1) {
            parcel.readLongArray(mTimestamp);
        }
        parcel.readIntArray(mElapsedTimestamp);
        parcel.readFloatArray(mAccX);
        parcel.readFloatArray(mAccY);
        parcel.readFloatArray(mAccZ);
        mStartTimestamp = parcel.readLong();
        if(parcel.readByte() == 1) {
            mProcessedX = new float[mIndex];
            mProcessedY = new float[mIndex];
            mProcessedZ = new float[mIndex];
            parcel.readFloatArray(mProcessedX);
            parcel.readFloatArray(mProcessedY);
            parcel.readFloatArray(mProcessedZ);
        }

        if(parcel.readByte() == 1) {
            mTrialDataIds = new int[mIndex];
            parcel.readIntArray(mTrialDataIds);
        }
    }

    public void addProcessedData(float[] processedX, float[] processedY, float[] processedZ) {
        mProcessedX = processedX;
        mProcessedY = processedY;
        mProcessedZ = processedZ;
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

    public final float[] getProcessedX() { return mProcessedX; }

    public final float[] getProcessedY() { return mProcessedY; }

    public final float[] getProcessedZ() {
        return mProcessedZ;
    }

    public final float[] getAccelDataY() {
        return mAccY;
    }

    public final float[] getAccelDataZ() {
        return mAccZ;
    }

    public final int[] getTrialDataIds() {
        return mTrialDataIds;
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

        if(mTimestamp != null) {
            dest.writeByte((byte)1);
            long[] timestamp = Arrays.copyOfRange(mTimestamp, 0, mIndex);
            dest.writeLongArray(timestamp);
        }
        else {
            dest.writeByte((byte)0);
        }
        int[] elapsed = Arrays.copyOfRange(mElapsedTimestamp, 0, mIndex);
        float[] x = Arrays.copyOfRange(mAccX, 0, mIndex);
        float[] y = Arrays.copyOfRange(mAccY, 0, mIndex);
        float[] z = Arrays.copyOfRange(mAccZ, 0, mIndex);

        dest.writeIntArray(elapsed);
        dest.writeFloatArray(x);
        dest.writeFloatArray(y);
        dest.writeFloatArray(z);
        dest.writeLong(mStartTimestamp);

        if(mProcessedX != null) {
            dest.writeByte((byte) 1);
            float[] procX = Arrays.copyOfRange(mProcessedX, 0, mIndex);
            float[] procY = Arrays.copyOfRange(mProcessedY, 0, mIndex);
            float[] procZ = Arrays.copyOfRange(mProcessedZ, 0, mIndex);
            dest.writeFloatArray(procX);
            dest.writeFloatArray(procY);
            dest.writeFloatArray(procZ);
        }
        else {
            dest.writeByte((byte)0);
        }

        if(mTrialDataIds != null) {
            dest.writeByte((byte) 1);
            int[] ids = Arrays.copyOfRange(mTrialDataIds, 0, mIndex);
            dest.writeIntArray(ids);
        }
        else {
            dest.writeByte((byte)0);
        }
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

    public float getMeanAccelValue(float[] data) {
        float sum = 0.0f;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }

        return (sum / data.length);
    }


}
