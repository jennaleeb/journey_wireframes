package com.uoft.journey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.uoft.journey.entity.AccelerometerData;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service for collecting the accelerometer data
 * Modified from MyWalk
 */
public class SensorService extends Service implements SensorEventListener {
    public static final String TAG = SensorService.class.getSimpleName();

    public static final String  ACTION_ACCELEROMETER_DATA = "ca.mywalk.app.action.ACCELEROMETER_DATA";

    public static boolean isRunning = false;

    private static final int HANDLER_SEND_DATA = 1000;
    private static final int DATA_SIZE = 100;
    private static final int TIME_DELAY_MILLIS = 150;

    private Context mContext;
    private AccelerometerData[] mData;
    private ReentrantLock mLock;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case HANDLER_SEND_DATA:
                    sendData();
                    sendDataDelayed(TIME_DELAY_MILLIS);
                    break;
            }
            super.handleMessage(msg);
        }

        private void sendData() {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while(!mLock.tryLock(100, TimeUnit.MILLISECONDS));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AccelerometerData data_tx = mData[0];
                    mData[0] = mData[1];
                    mData[0].setStartTimestamp(data_tx.getStartTimestamp());
                    mData[1] = new AccelerometerData(DATA_SIZE, data_tx.getStartTimestamp());
                    mLock.unlock();
                    Intent intent = new Intent();
                    intent.setAction(ACTION_ACCELEROMETER_DATA);
                    intent.putExtra(AccelerometerData.ACCELEROMETER_DATA_KEY, data_tx);

                    String output = "";
                    for(int i=0; i< mData[1].getDataCount(); i++) {
                        // Add line for file output
                        output += String.format("%s, %f, %f, %f\n", Long.toString(mData[1].getTimestamps()[i]),
                                mData[1].getAccelDataX()[i], mData[1].getAccelDataY()[i], mData[1].getAccelDataZ()[i]);
                    }

                    try {
                        // Save to file
                        FileOutputStream fos = openFileOutput("data.csv", MODE_APPEND);
                        fos.write(output.getBytes());
                        fos.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                    mContext.sendBroadcast(intent);
                }

            }).start();
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mLock = new ReentrantLock();
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(HANDLER_SEND_DATA);
        mSensorManager.unregisterListener(this);
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File file = new File("data.csv");
        file.delete();

        mData = new AccelerometerData[2];
        mData[0] = new AccelerometerData(DATA_SIZE, -1);
        mData[1] = new AccelerometerData(DATA_SIZE, -1);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        sendDataDelayed(TIME_DELAY_MILLIS);
        isRunning = true;
        return Service.START_STICKY;
    }

    private void sendDataDelayed(long delayMillis) {
        Message msg = new Message();
        msg.what = HANDLER_SEND_DATA;
        mHandler.sendMessageDelayed(msg, delayMillis);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensor_type = event.sensor.getType();
        if(sensor_type == Sensor.TYPE_ACCELEROMETER) {
            try {
                while(!mLock.tryLock(100, TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mData[0].addData(event.timestamp,
                    event.values[0],
                    event.values[1],
                    event.values[2]);
            mLock.unlock();
        }
    }
}
