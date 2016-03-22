package com.uoft.journey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;

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

    private SignalProcessTask mTask;
    private Context mContext;
    public static boolean isRunning = false;
    private Trial mTrial;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        mTrial = extras.getParcelable("trial");

        isRunning = true;
        mTask = new SignalProcessTask();
        mTask.execute(mTrial);
        return START_STICKY;
    }

    private void sendResult(Trial data) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PROCESSED_DATA);
        intent.putExtra(Trial.TRIAL_DATA_KEY, data);
        isRunning = false;
        mContext.sendBroadcast(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SignalProcessTask extends AsyncTask<Trial, Void, Trial> {

        @Override
        protected Trial doInBackground(Trial... params) {

            Trial trial = params[0];
            try{
                // Perform smoothing on X,Y,Z
                trial.getTrialData().addProcessedData(Gait.simpleLowPassFilter(trial.getTrialData().getAccelDataX(), 50),
                        Gait.butterworthFilter(trial.getTrialData().getAccelDataY(), 60.0, 4, 4.0, 1.0),
                        Gait.simpleLowPassFilter(trial.getTrialData().getAccelDataZ(), 50));

                // Identify steps
                Integer[] steps = Gait.localMaximaTimesUsingWindow(trial.getTrialData().getProcessedY(), trial.getTrialData().getElapsedTimestamps());
                int[] stepTimes = new int[steps.length];
                for(int i=0; i<steps.length; i++) {
                    stepTimes[i] = steps[i];
                }
                trial.setStepTimes(stepTimes);
                trial.setPauseTimes(Gait.getPausePoints(stepTimes));
                float mean = Gait.getMeanStepTime(stepTimes, trial.getPauseTimes());
                float sd = Gait.getStandardDeviation(stepTimes, mean, trial.getPauseTimes());
                float var = Gait.getCoefficientOfVariation(sd, mean);
                trial.setStepAnalysis(mean, sd, var);

                // Save the processed data
                LocalDatabaseAccess.updateTrial(mContext, trial);
                LocalDatabaseAccess.updateProcessedTrialData(mContext, trial.getTrialData());
                LocalDatabaseAccess.addTrialSteps(mContext, trial.getTrialId(), trial.getStepTimes(), trial.getPauseTimes());

                return trial;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Trial result) {
            sendResult(result);
        }
    }
}
