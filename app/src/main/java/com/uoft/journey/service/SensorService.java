package com.uoft.journey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.data.LocalDatabaseHelper;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;

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

    public static final String  ACTION_ACCELEROMETER_DATA = "com.uoft.journey.action.ACCELEROMETER_DATA";

    public static boolean isRunning = false;

    private static final int HANDLER_SEND_DATA = 1000;
    private static final int DATA_SIZE = 100;
    private static final int TIME_DELAY_MILLIS = 150;

    private Context mContext;
    private AccelerometerData[] mData;
    private ReentrantLock mLock;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int mTrialId;

    // Metronome
    private short spm = 100;
    private short noteValue = 4;
    private short beats = 1;
    private double beatSound = 2440;
    private double sound = 6440;
    private Handler mMetroHandler;
    private MetronomeAsyncTask metroTask;

    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = (String)msg.obj;
                // Do something on each beat???

            }
        };
    }

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

                    // Save entries to DB
                    LocalDatabaseAccess.addTrialData(mContext, mTrialId, data_tx);

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
        metroTask = new MetronomeAsyncTask();
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(HANDLER_SEND_DATA);
        mSensorManager.unregisterListener(this);
        metroTask.stop();
        metroTask = new MetronomeAsyncTask();
        isRunning = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        mTrialId = extras.getInt("trialId");
        spm = extras.getShort("spm");

        mData = new AccelerometerData[2];
        mData[0] = new AccelerometerData(DATA_SIZE, -1);
        mData[1] = new AccelerometerData(DATA_SIZE, -1);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
        sendDataDelayed(TIME_DELAY_MILLIS);
        isRunning = true;

        if(spm > 0) {
            // We want to run multiple async tasks, force this on Honeycomb or greater version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                metroTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
            else
                metroTask.execute();
        }

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


    private class MetronomeAsyncTask extends AsyncTask<Void,Void,String> {
        Metronome metronome;

        MetronomeAsyncTask() {
            mMetroHandler = getHandler();
            metronome = new Metronome(mMetroHandler);
        }

        protected String doInBackground(Void... params) {
            metronome.setBeat(beats);
            metronome.setNoteValue(noteValue);
            metronome.setBpm(spm);
            metronome.setBeatSound(beatSound);
            metronome.setSound(sound);

            metronome.play();

            return null;
        }

        public void stop() {
            metronome.stop();
            metronome = null;
        }

        public void setBpm(short bpm) {
            metronome.setBpm(bpm);
            metronome.calcSilence();
        }

        public void setBeat(short beat) {
            if(metronome != null)
                metronome.setBeat(beat);
        }

    }
}
