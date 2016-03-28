package com.uoft.journey.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.uoft.journey.Journey;
import com.uoft.journey.R;
import com.uoft.journey.data.ServerAccess;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataProcessingService;
import com.uoft.journey.service.DataService;
import com.uoft.journey.service.SensorService;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MeasureActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, MediaPlayer.OnCompletionListener {

    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnDone;
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
    private TextView mStartWalking;
    private CheckBox mTimedCheck;
    private Spinner mTimeSpinner;
    private RelativeLayout mTimedLayout;
    private long mTimedSecs = 0;
    private long mStartTime = 0;
    private CheckBox mMetroCheck;
    private SeekBar mMetroSeek;
    private RelativeLayout mMetroLayout;
    Journey mApp;
    private String mUsername;
    //private Journey mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        //Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();
        /*mUserId = extras.getInt("userId");
        mUsername = extras.getString("username");*/
        mUserId = intent.getIntExtra("userId",0);

        mUsername = intent.getStringExtra("username");
        System.out.println("MEASURE1 THE USERNAME IS: " + mUsername);


        mApp = ((Journey)getApplicationContext());

        if(savedInstanceState != null) {
            mTrial = savedInstanceState.getParcelable("trial");
            mCountdownTimeRemaining = savedInstanceState.getLong("countdown");
            mFinished = savedInstanceState.getBoolean("finished");
            mStartTime = savedInstanceState.getLong("startTime");
            mTimedSecs = savedInstanceState.getLong("timedSecs");
        }

        mWalkImage = (ImageView)findViewById(R.id.image_walk);
        mWalkAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mBtnStart = (Button)findViewById(R.id.button_start);
        mBtnStart.setOnClickListener(this);
        mBtnStop = (Button)findViewById(R.id.button_stop);
        mBtnStop.setOnClickListener(this);
        mBtnDone = (Button)findViewById(R.id.button_done);
        mBtnDone.setOnClickListener(this);
        mBtnDone.setEnabled(false);
        mProgress = (ProgressBar)findViewById(R.id.pb_spinner);
        mProcessingText = (TextView)findViewById(R.id.text_processing);
        mCountdown = (TextView)findViewById(R.id.text_countdown);
        mInstructions = (TextView)findViewById(R.id.text_instructions);
        mStartWalking = (TextView)findViewById(R.id.text_walk_instructions);
        mTimedCheck = (CheckBox)findViewById(R.id.check_timed);
        mTimedCheck.setOnCheckedChangeListener(this);
        mTimeSpinner = (Spinner)findViewById(R.id.spin_time);
        mTimeSpinner.setEnabled(false);
        mTimedLayout = (RelativeLayout)findViewById(R.id.layout_timed);
        mMetroCheck = (CheckBox)findViewById(R.id.check_metro);
        mMetroCheck.setOnCheckedChangeListener(this);
        mMetroSeek = (SeekBar)findViewById(R.id.seek_metro);
        mMetroSeek.setEnabled(false);
        mMetroLayout = (RelativeLayout)findViewById(R.id.layout_metro);

        // Assessment complete, so just show results
        if(mFinished) {
            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mInstructions.setVisibility(View.INVISIBLE);
            mTimedLayout.setVisibility(View.INVISIBLE);
            mMetroLayout.setVisibility(View.INVISIBLE);
            mStartWalking.setVisibility(View.INVISIBLE);
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
            mTimedLayout.setVisibility(View.INVISIBLE);
            mMetroLayout.setVisibility(View.INVISIBLE);
            if(SensorService.isRunning) {
                mWalkImage.setVisibility(View.VISIBLE);
                mWalkImage.startAnimation(mWalkAnim);
                mStartWalking.setText(R.string.keep_walking);
                mStartWalking.setVisibility(View.VISIBLE);
            }
            else {
                mProgress.setVisibility(View.VISIBLE);
                mProcessingText.setVisibility(View.VISIBLE);
                mStartWalking.setVisibility(View.INVISIBLE);
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
            //e.printStackTrace();
        }

        try {
            getApplicationContext().unregisterReceiver(mProcessReceiver);
        }
        catch (Exception e) {
            //e.printStackTrace();
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
        savedInstanceState.putLong("startTime", mStartTime);
        savedInstanceState.putLong("timedSecs", mTimedSecs);
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
            case R.id.button_done:
                finish();
                break;
        }
    }

    private void startCollecting() {
        if(!SensorService.isRunning && !DataProcessingService.isRunning) {
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mStartWalking.setText(R.string.start_walking);
            mStartWalking.setVisibility(View.VISIBLE);
            mCountdownTimer.start();
        }
    }

    private void startDataCollect() {
        if(!SensorService.isRunning && !DataProcessingService.isRunning) {
            // Play alert sound and vibrate for 500 milliseconds
            try {
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notification0);
                mp.start();
                mp.setOnCompletionListener(this);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mBtnStop.setEnabled(true);
            mBtnStop.setVisibility(View.VISIBLE);
            mBtnStart.setEnabled(false);
            mBtnStart.setVisibility(View.INVISIBLE);
            mWalkImage.setVisibility(View.VISIBLE);
            mWalkImage.startAnimation(mWalkAnim);
            mInstructions.setVisibility(View.INVISIBLE);
            mTimedLayout.setVisibility(View.INVISIBLE);
            mMetroLayout.setVisibility(View.INVISIBLE);
            mStartWalking.setText(R.string.keep_walking);
            mStartWalking.setVisibility(View.VISIBLE);

            // Create the trial
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            Date start = cal.getTime();
            System.out.println("MEASURE2 THE USERNAME IS: " + mUsername);

            int trialId = DataService.addNewTrial(this, mUserId, start, mUsername);
            mTrial = new Trial(trialId, start, null, mUsername);

            // If it's a timed trial
            mStartTime = System.currentTimeMillis();
            mTimedSecs = 300000; // Default to 5 mins max
            if(mTimedCheck.isChecked()) {
                switch(mTimeSpinner.getSelectedItemPosition()) {
                    case 0:
                        mTimedSecs = 5000;
                        break;
                    case 1:
                        mTimedSecs = 10000;
                        break;
                    case 2:
                        mTimedSecs = 20000;
                        break;
                    case 3:
                        mTimedSecs = 30000;
                        break;
                    case 4:
                        mTimedSecs = 60000;
                        break;
                    case 5:
                        mTimedSecs = 90000;
                        break;
                    case 6:
                        mTimedSecs = 120000;
                        break;
                    case 7:
                        mTimedSecs = 180000;
                        break;
                    case 8:
                        mTimedSecs = 240000;
                        break;
                    case 9:
                        mTimedSecs = 300000;
                        break;
                }
            }

            short stepsPerMin = 0;
            if(mMetroCheck.isChecked()) {
                stepsPerMin = (short)(40 + (mMetroSeek.getProgress() * 15));
            }

            // Call the service to start collecting accelerometer data
            Intent intent = new Intent(this, SensorService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("trialId", trialId);
            bundle.putShort("spm", stepsPerMin);
            intent.putExtras(bundle);
            startService(intent);
            getApplicationContext().registerReceiver(mReceiver, mIntentFilter);
        }
    }


    private void stopCollecting() {
        if (SensorService.isRunning) {
            // Play alert sound and vibrate for 500 milliseconds
            try {
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notification0);
                mp.start();
                mp.setOnCompletionListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mBtnStop.setEnabled(false);
            mBtnStop.setVisibility(View.INVISIBLE);
            mWalkImage.clearAnimation();
            mWalkImage.setVisibility(View.INVISIBLE);
            mStartWalking.setVisibility(View.INVISIBLE);

            stopService(new Intent(this, SensorService.class));
            SensorService.isRunning = false;
            try {
                getApplicationContext().unregisterReceiver(mReceiver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            // Wait for final data to save before processing
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTrial.setTrialData(DataService.getTrialData(getApplicationContext(), mTrial.getTrialId()));
                    startProcessing();
                }
            }, 500);
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
            System.out.println("MEASUREACCTIVITY3 THE USERNAME IS: " + mUsername);
            mTrial.setUsername(mUsername);

            ServerAccess.addTrial(getApplicationContext(), mTrial.getTrialId(), mUsername);
            showResults();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResults() {
        ((TextView)findViewById(R.id.text_output_1_val)).setText(String.format("%d", mTrial.getDuration() / 1000));
        ((TextView)findViewById(R.id.text_output_2_val)).setText(String.format("%d", mTrial.getNumberOfSteps()));
        ((TextView)findViewById(R.id.text_output_3_val)).setText(String.format("%.0f", mTrial.getMeanStrideTime()));

        TextView stv = (TextView) findViewById(R.id.text_output_4_val);
        stv.setText(String.format("%.1f", mTrial.getCoeffOfVar()));

        Trial.Level level = Trial.getLevel(mTrial.getCoeffOfVar());
        switch (level) {
            case GOOD:
                stv.setBackgroundResource(R.drawable.round_text_green);
                break;
            case OK:
                stv.setBackgroundResource(R.drawable.round_text_yellow);
                break;
            case BAD:
                stv.setBackgroundResource(R.drawable.round_text_red);
                break;
        }

        findViewById(R.id.layout_output).setVisibility(View.VISIBLE);
        mBtnDone.setVisibility(View.VISIBLE);
        mBtnDone.setEnabled(true);
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
                // If it's a timed trial check if it should finish
                if(mTimedSecs > 0 && System.currentTimeMillis() >= mStartTime + mTimedSecs) {
                    stopCollecting();
                }
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
                DataProcessingService.isRunning = false;
                outputProcessedResults(result);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.check_timed) {
            mTimeSpinner.setEnabled(isChecked);
        }

        if(buttonView.getId() == R.id.check_metro) {
            mMetroSeek.setEnabled(isChecked);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mp!=null) {
            if(mp.isPlaying())
                mp.stop();
            mp.reset();
            mp.release();
            mp=null;
        }
    }
}
