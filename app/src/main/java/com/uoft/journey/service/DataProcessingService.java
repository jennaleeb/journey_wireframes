package com.uoft.journey.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.Trial;

/**
 * Signal processing of accelerometer data
 */
public class DataProcessingService extends Service {

    public static final String  ACTION_PROCESSED_DATA = "com.uoft.journey.action.PROCESSED_DATA";
    public static final String TAG = "Data Processing Tag - ";

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
        final Intent intent = new Intent();
        intent.setAction(ACTION_PROCESSED_DATA);
        intent.putExtra(Trial.TRIAL_DATA_KEY, data);

        final Handler h = new Handler();
        final int delay = 1000; //milliseconds
        final long firstTime = System.currentTimeMillis();

        // Keep trying to send the result, in case the activity is paused
        h.postDelayed(new Runnable(){
            public void run(){
                // Try for 100 seconds
                if(isRunning && System.currentTimeMillis() - 100000 < firstTime) {
                    mContext.sendBroadcast(intent);
                    h.postDelayed(this, delay);
                }
                else {
                    isRunning = false;
                }
            }
        }, delay);
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

                if (stepTimes.length > 0) {
                    trial.setStepTimes(stepTimes);
                    trial.setPauseTimes(Gait.getPausePoints(stepTimes));
                    float mean_step = Gait.getMeanStepTime(stepTimes, trial.getPauseTimes());
                    float step_sd = Gait.getStepSD(stepTimes, mean_step, trial.getPauseTimes());
                    float step_cv = Gait.getStepCV(step_sd, mean_step);
                    float sym = Gait.getGaitSymmetry(stepTimes, trial.getPauseTimes());
                    float mean_stride = Gait.getMeanStrideTime(stepTimes, trial.getPauseTimes());
                    float cadence = trial.getCadence();
                    float stride_sd = Gait.getStrideSD(stepTimes, mean_stride, trial.getPauseTimes());
                    float stride_cv = Gait.getStrideCV(stride_sd, mean_stride);
                    trial.setStepAnalysis(mean_step, step_sd, step_cv, sym, cadence, mean_stride, stride_sd, stride_cv);
                    trial.getGame_played();
                }

                // Experimenting with RMS
//                float RMS = Gait.getRMS(trial.getTrialData().getProcessedY(), trial.getTrialData().getMeanAccelValue(trial.getTrialData().getProcessedY()));
//                Log.d(TAG, "a[0] " + trial.getTrialData().getProcessedY()[0]);
//                Log.d(TAG, "a[n] " + trial.getTrialData().getProcessedY()[trial.getTrialData().getProcessedY().length - 1]);
//                Log.d(TAG, "N = " + trial.getTrialData().getProcessedY().length);
//                Log.d(TAG, "RMS: " + RMS );
//
//                // Experimenting with Fourier Transform and Harmonic ratio
//                float[] data = trial.getTrialData().getProcessedY();
//                DoubleFFT_1D fft1 = new DoubleFFT_1D(data.length);
//                fft1.realForward(Gait.convertFloatsToDoubles(data));
//
//                DoubleFFT_1D fft2 = new DoubleFFT_1D(data.length);
//                fft2.realForwardFull(Gait.convertFloatsToDoubles(data));

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
