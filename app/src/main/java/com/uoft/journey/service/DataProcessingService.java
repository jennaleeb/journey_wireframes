package com.uoft.journey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.uoft.journey.entity.AccelerometerData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Signal processing of accelerometer data
 */
public class DataProcessingService extends Service {

    public static final String  ACTION_PROCESSED_DATA = "com.uoft.journey.action.PROCESSED_DATA";

//    private final static int WINDOW_SHIFT_MS = 100;
//    private final static int WINDOW_SIZE_MS = 3000;
    private SignalProcessTask mTask;
    private Context mContext;
    public static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        mTask = new SignalProcessTask();
        mTask.execute("data.csv");
        return START_STICKY;
    }

    private void sendResult(AccelerometerData data) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PROCESSED_DATA);
        intent.putExtra(AccelerometerData.ACCELEROMETER_DATA_KEY, data);
        isRunning = false;
        mContext.sendBroadcast(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SignalProcessTask extends AsyncTask<String, Void, AccelerometerData> {

        @Override
        protected AccelerometerData doInBackground(String... params) {
            String filename = params[0];
            ArrayList<Integer> elapsedVals = new ArrayList<>();
            ArrayList<Long> timeVals = new ArrayList<>();
            ArrayList<Float> xVals = new ArrayList<>();
            ArrayList<Float> yVals = new ArrayList<>();
            ArrayList<Float> zVals = new ArrayList<>();

            try {
                FileInputStream fis = openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    elapsedVals.add(Integer.parseInt(line.substring(0, line.indexOf(","))));
                    line = line.substring(line.indexOf(",") + 1);
                    timeVals.add(Long.parseLong(line.substring(0, line.indexOf(","))));
                    line = line.substring(line.indexOf(",") + 1);
                    xVals.add(Float.parseFloat(line.substring(0, line.indexOf(","))));
                    line = line.substring(line.indexOf(",") + 1);
                    yVals.add(Float.parseFloat(line.substring(0, line.indexOf(","))));
                    zVals.add(Float.parseFloat(line.substring(line.indexOf(",") + 1)));
                }

                isr.close();

                //int maxElapsed = elapsedVals.get(elapsedVals.size() - 1);
                //boolean isFinished = false;

                float[] xArray = new float[xVals.size()];
                for(int i=0; i<xVals.size(); i++) {
                    xArray[i] = xVals.get(i);
                }

                xArray = Gait.simpleLowPassFilter(xArray, 20.0f);

                float[] yArray = new float[yVals.size()];
                for(int i=0; i<yVals.size(); i++) {
                    yArray[i] = yVals.get(i);
                }

                yArray = Gait.simpleLowPassFilter(yArray, 20.0f);

                float[] zArray = new float[zVals.size()];
                for(int i=0; i<zVals.size(); i++) {
                    zArray[i] = zVals.get(i);
                }

                zArray = Gait.simpleLowPassFilter(zArray, 20.0f);

                int[] elArray = new int[elapsedVals.size()];
                for(int i=0; i<elapsedVals.size(); i++) {
                    elArray[i] = elapsedVals.get(i);
                }
                long[] timeArray = new long[timeVals.size()];
                for(int i=0; i<timeVals.size(); i++) {
                    timeArray[i] = timeVals.get(i);
                }

                return new AccelerometerData(timeArray, elArray, xArray, yArray, zArray);

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(AccelerometerData result) {
            sendResult(result);
        }
    }
}
