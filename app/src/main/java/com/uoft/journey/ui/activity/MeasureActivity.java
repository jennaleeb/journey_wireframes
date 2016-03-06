package com.uoft.journey.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uoft.journey.R;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataProcessingService;
import com.uoft.journey.service.DataService;
import com.uoft.journey.service.SensorService;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MeasureActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnStart;
    private Button mBtnStop;
    private IntentFilter mIntentFilter;
    private IntentFilter mProcessIntentFilter;
    private Trial mTrial;
    private int mUserId;
    private ProgressBar mProgress;
    private TextView mProcessingText;
    private TextView mCountdown;
    private TextView mInstructions;
    private ImageView mWalkImage;
    private Animation mWalkAnim;
    private CountDownTimer mCountdownTimer;
    private long mCountdownTimeRemaining = -1;
    private boolean mFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        Bundle extras = getIntent().getExtras();
        mUserId = extras.getInt("userId");

        if(savedInstanceState != null) {
            mTrial = savedInstanceState.getParcelable("trial");
            mCountdownTimeRemaining = savedInstanceState.getLong("countdown");
            mFinished = savedInstanceState.getBoolean("finished");
        }

        mWalkImage = (ImageView)findViewById(R.id.image_walk);
        mWalkAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mBtnStart = (Button)findViewById(R.id.button_start);
        mBtnStart.setOnClickListener(this);
        mBtnStop = (Button)findViewById(R.id.button_stop);
        mBtnStop.setOnClickListener(this);
        mProgress = (ProgressBar)findViewById(R.id.pb_spinner);
        mProcessingText = (TextView)findViewById(R.id.text_processing);
        mCountdown = (TextView)findViewById(R.id.text_countdown);
        mInstructions = (TextView)findViewById(R.id.text_instructions);

        // Assessment complete, so just show results
        if(mFinished) {
            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mInstructions.setVisibility(View.INVISIBLE);
            showResults();
            return;
        }


        if(!SensorService.isRunning && !DataProcessingService.isRunning) {
            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
        }
        else {
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mInstructions.setVisibility(View.INVISIBLE);
            if(SensorService.isRunning) {
                mWalkImage.setVisibility(View.VISIBLE);
                mWalkImage.startAnimation(mWalkAnim);
            }
            else {
                mProgress.setVisibility(View.VISIBLE);
                mProcessingText.setVisibility(View.VISIBLE);
            }
        }

        long maxTime = 5100;
        if(mCountdownTimeRemaining > 0)
            maxTime = mCountdownTimeRemaining;

        // Countdown timer gets called when start is pressed to show countdown
        mCountdownTimer = new CountDownTimer(maxTime, 1000) {

            public void onTick(long millisUntilFinished) {
                mCountdown.setText(String.format("%d", millisUntilFinished / 1000));
                mCountdownTimeRemaining = millisUntilFinished;
            }

            public void onFinish() {
                mCountdown.setText("");
                startDataCollect();
                mCountdownTimeRemaining = -1;
            }
        };

        mIntentFilter = new IntentFilter(SensorService.ACTION_ACCELEROMETER_DATA);
        mProcessIntentFilter = new IntentFilter(DataProcessingService.ACTION_PROCESSED_DATA);

        // If countdown timer already running
        if(mCountdownTimeRemaining > 0)
            startCollecting();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mCountdownTimer != null)
            mCountdownTimer.cancel();

        try {
            getApplicationContext().unregisterReceiver(mReceiver);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            getApplicationContext().unregisterReceiver(mProcessReceiver);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SensorService.isRunning) {
            try {
                getApplicationContext().registerReceiver(mReceiver, mIntentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(DataProcessingService.isRunning) {
            try {
                getApplicationContext().registerReceiver(mProcessReceiver, mProcessIntentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if(mCountdownTimer != null)
            mCountdownTimer.cancel();

        if(SensorService.isRunning) {
            try {
                stopService(new Intent(this, SensorService.class));
                getApplicationContext().unregisterReceiver(mReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(DataProcessingService.isRunning) {
            try {
                stopService(new Intent(this, DataProcessingService.class));
                getApplicationContext().unregisterReceiver(mProcessReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current trial
        savedInstanceState.putParcelable("trial", mTrial);
        savedInstanceState.putLong("countdown", mCountdownTimeRemaining);
        savedInstanceState.putBoolean("finished", mFinished);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_start:
                startCollecting();
                break;
            case R.id.button_stop:
                stopCollecting();
                break;
        }
    }

    private void startCollecting() {
        if(!SensorService.isRunning && !DataProcessingService.isRunning) {
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mCountdownTimer.start();
        }
    }

    private void startDataCollect() {
        if(!SensorService.isRunning && !DataProcessingService.isRunning) {
            mBtnStop.setEnabled(true);
            mBtnStop.setVisibility(View.VISIBLE);
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mWalkImage.setVisibility(View.VISIBLE);
            mWalkImage.startAnimation(mWalkAnim);
            mInstructions.setVisibility(View.INVISIBLE);

            // Create the trial
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            Date start = cal.getTime();
            int trialId = DataService.addNewTrial(this, mUserId, start);
            mTrial = new Trial(trialId, start, null);

            // Call the service to start collecting accelerometer data
            Intent intent = new Intent(this, SensorService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("trialId", trialId);
            intent.putExtras(bundle);
            startService(intent);
            getApplicationContext().registerReceiver(mReceiver, mIntentFilter);
        }
    }


    private void stopCollecting() {
        if (SensorService.isRunning) {
            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
            mWalkImage.clearAnimation();
            mWalkImage.setVisibility(View.INVISIBLE);

            stopService(new Intent(this, SensorService.class));
            SensorService.isRunning = false;
            mTrial.setTrialData(DataService.getTrialData(this, mTrial.getTrialId()));
            try {
                getApplicationContext().unregisterReceiver(mReceiver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            startProcessing();
        }
    }

    private void startProcessing() {
        if(!SensorService.isRunning && !DataProcessingService.isRunning) {
            mProgress.setVisibility(View.VISIBLE);
            mProcessingText.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, DataProcessingService.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("trial", mTrial);
            intent.putExtras(bundle);
            startService(intent);
            getApplicationContext().registerReceiver(mProcessReceiver, mProcessIntentFilter);
        }
    }

    private void stopProcessing() {
        try {
            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
            stopService(new Intent(this, DataProcessingService.class));
            getApplicationContext().unregisterReceiver(mProcessReceiver);
            mProgress.setVisibility(View.INVISIBLE);
            mProcessingText.setVisibility(View.INVISIBLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outputProcessedResults(Trial trial) {
        try {
            mFinished = true;
            stopProcessing();
            mTrial = trial;
            showResults();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResults() {
        ((TextView)findViewById(R.id.text_output_1)).setText(String.format("Assessment duration: %ds", mTrial.getDuration() / 1000));
        ((TextView)findViewById(R.id.text_output_2)).setText(String.format("Number of steps: %d", mTrial.getNumberOfSteps()));
        ((TextView)findViewById(R.id.text_output_3)).setText(String.format("Average stride time: %.2fms", mTrial.getMeanStrideTime()));
        ((TextView)findViewById(R.id.text_output_4)).setText(String.format("Standard deviation: %.2fms", mTrial.getStandardDev()));
        ((TextView)findViewById(R.id.text_output_5)).setText(String.format("Coefficient of Variation: %.2f", mTrial.getCoeffOfVar()));
        findViewById(R.id.layout_output).setVisibility(View.VISIBLE);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new HandleUpdate().execute(intent);
        }

        class HandleUpdate extends AsyncTask<Intent, Void, AccelerometerData> {
            @Override
            protected AccelerometerData doInBackground(Intent ... params) {
                return (AccelerometerData)params[0].getParcelableExtra(AccelerometerData.ACCELEROMETER_DATA_KEY);
            }

            @Override
            protected void onPostExecute(AccelerometerData result) {
                // If we want to output any data as it's being collected
            }
        }
    };

    BroadcastReceiver mProcessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new HandleUpdate().execute(intent);
        }

        class HandleUpdate extends AsyncTask<Intent, Void, Trial> {
            @Override
            protected Trial doInBackground(Intent ... params) {
                return params[0].getParcelableExtra(Trial.TRIAL_DATA_KEY);
            }

            @Override
            protected void onPostExecute(Trial result) {
                outputProcessedResults(result);
            }
        }
    };
}
