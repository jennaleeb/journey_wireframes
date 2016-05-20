package com.uoft.journey.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.uoft.journey.R;
import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.entity.AccelerometerData;
import com.uoft.journey.entity.InhibitionGame;
import com.uoft.journey.entity.Trial;
import com.uoft.journey.service.DataService;
import com.uoft.journey.service.Gait;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class AssessmentDetailActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private Trial mTrial;
    private InhibitionGame mInhibGame;
    private LineChart mAccelGraph;
    private LineChart mStepTimeGraph;
    private String mUsername;
    private Button mDeleteStepButton;
    public static String TAG = "GraphDetailTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_assessment_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        int trialId = extras.getInt("trialId");
        mUsername = extras.getString("user");
        getSupportActionBar().setTitle(String.format("Assessment %d", trialId));
        mTrial = DataService.getTrial(this, trialId, mUsername);
        int[] steps = DataService.getTrialStepTimes(this, trialId);

        mInhibGame = DataService.getGameByTrialId(this, trialId, mUsername);

        mAccelGraph = (LineChart)findViewById(R.id.detail_graph);
        mStepTimeGraph = (LineChart)findViewById(R.id.step_time_graph);
        setupGraph();

        if(mTrial != null) {
            populateTrial();
            plotGraph();
        }
        else {
            Toast.makeText(this, "Failed to load assessment", Toast.LENGTH_SHORT).show();
        }

        if (mInhibGame != null) {
            loadGameStats();
        }

        else {
            showBlankCard();
        }
    }

    private void loadGameStats() {
        ((TextView)findViewById(R.id.text_game_id)).setText("Trial " + mInhibGame.getTrialId() + ", Game " + mInhibGame.getGameId());
        ((TextView)findViewById(R.id.text_hit_count_val)).setText(String.format("%d", mInhibGame.getHitCount()));
        ((TextView)findViewById(R.id.text_miss_count_val)).setText(String.format("%d", mInhibGame.getMissCount()));
        ((TextView)findViewById(R.id.text_false_alarm_count_val)).setText(String.format("%d", mInhibGame.getFalseAlarmCount()));
        ((TextView)findViewById(R.id.text_correct_neg_count_val)).setText(String.format("%d", mInhibGame.getCorrectNegCount()));
        ((TextView)findViewById(R.id.text_game_stat_1_val)).setText(String.format("%d", mInhibGame.getMeanResponseTime()));
        ((TextView)findViewById(R.id.text_game_stat_2_val)).setText(String.format("%.3f", mInhibGame.getOverallAccuracy()));
        ((TextView)findViewById(R.id.text_game_stat_3_val)).setText(String.format("%.0f", mInhibGame.getOmissionError()));
        ((TextView)findViewById(R.id.text_game_stat_4_val)).setText(String.format("%.0f", mInhibGame.getCommissionError()));
    }

    private void showBlankCard() {
        CardView card = (CardView)findViewById(R.id.card_game_stats);
        card.setVisibility(View.GONE);
    }

    private void populateTrial() {
        DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.CANADA);
        ((TextView)findViewById(R.id.text_detail_date)).setText(df.format(mTrial.getStartTime()) + " - " + String.format("%d", mTrial.getDuration() / 1000) + " sec");
        ((TextView)findViewById(R.id.text_detail_2_val)).setText(String.format("%d", mTrial.getNumberOfSteps()));
        ((TextView)findViewById(R.id.text_detail_5_val)).setText(String.format("%.1f", mTrial.getGaitSym()));
        ((TextView)findViewById(R.id.text_detail_6_val)).setText(String.format("%.1f", mTrial.getMeanStrideTime()));

        TextView stv = (TextView) findViewById(R.id.text_detail_7_val);
        stv.setText(String.format("%.1f", mTrial.getStrideCV()));

        Trial.Level level = Trial.getLevel(mTrial.getStrideCV());

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

    }

    private void setupGraph() {
        mAccelGraph.setTouchEnabled(true);
        mAccelGraph.setOnChartValueSelectedListener(this);
        mAccelGraph.setDragEnabled(true);
        mAccelGraph.setScaleEnabled(true);
        mAccelGraph.setDrawGridBackground(false);
        mAccelGraph.setPinchZoom(true);
        LineData data = new LineData();
        data.setValueTextColor(Color.RED);
        mAccelGraph.setData(data);
        mAccelGraph.setVisibleXRange(0, 100);
        mAccelGraph.setVisibleYRangeMaximum(100, YAxis.AxisDependency.LEFT);

        // Y values
        LineDataSet setX = new LineDataSet(null, "movement");
        setX.setLineWidth(2.5f);
        setX.setDrawCircles(false);
        setX.setColor(Color.rgb(240, 99, 99));
        data.addDataSet(setX);

        // Steps
        LineDataSet setY = new LineDataSet(null, "steps");
        setY.setLineWidth(2.5f);
        setY.setDrawCircles(false);
        setY.setColor(Color.rgb(99, 240, 99));
        data.addDataSet(setY);

        mStepTimeGraph.setDragEnabled(true);
        mStepTimeGraph.setScaleEnabled(true);
        mStepTimeGraph.setDrawGridBackground(false);
        mStepTimeGraph.setPinchZoom(true);
        mStepTimeGraph.setAutoScaleMinMaxEnabled(true);
        LineData data2 = new LineData();
        mStepTimeGraph.setData(data2);



        // Step times
        LineDataSet setZ = new LineDataSet(null, "step time");
        setZ.setLineWidth(2.5f);
        setZ.setDrawCircles(false);
        setZ.setColor(Color.BLUE);
        setZ.setDrawValues(false);
        data2.addDataSet(setZ);

    }

    private void plotGraph() {

        try {

            AccelerometerData data = mTrial.getTrialData();

            if (data.getProcessedY().length != 0) {

                mAccelGraph.clear();
                mStepTimeGraph.clear();

                setupGraph();
                LineData graphData = mAccelGraph.getData();

                int nextStep = 0;
                for(int i=0; i< data.getProcessedY().length; i++) {
                    // Add timestamp to X-axis and line series values
                    graphData.addXValue(data.getElapsedTimestamps()[i] + "");
                    graphData.addEntry(new Entry(data.getProcessedY()[i], i), 0); // The processed Y data


                    boolean added = false;
                    if(mTrial.getStepTimes() != null) {
                        // Show the calculated maxima points
                        while (nextStep < mTrial.getStepTimes().length) {
                            if (mTrial.getStepTimes()[nextStep] == data.getElapsedTimestamps()[i]) {
                                // Step found, add a 10 point
                                graphData.addEntry(new Entry(10, i), 1);
                                nextStep++;
                                added = true;
                                break;
                            }

                            if (mTrial.getStepTimes()[nextStep] > data.getElapsedTimestamps()[i]) {
                                break;
                            }

                            nextStep++;
                        }
                    }
                    // Not a step so add zero point
                    if(!added) {
                        graphData.addEntry(new Entry(0, i), 1);
                    }

                    // Show any pauses
                    if(mTrial.getPauseTimes() != null) {
                        XAxis xAxis = mAccelGraph.getXAxis();
                        for(int j=0; j<mTrial.getPauseTimes().size(); j++) {
                            if(data.getElapsedTimestamps()[i] == mTrial.getPauseTimes().get(j)[0] ||
                                    (i < data.getElapsedTimestamps().length - 1 && data.getElapsedTimestamps()[i] < mTrial.getPauseTimes().get(j)[0] && data.getElapsedTimestamps()[i+1] > mTrial.getPauseTimes().get(j)[0] )) {

                                LimitLine ll = new LimitLine(i, "Pause");
                                ll.setLineColor(Color.YELLOW);
                                ll.setLineWidth(2f);
                                ll.setTextColor(Color.BLACK);
                                ll.setTextSize(10f);
                                ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                                xAxis.addLimitLine(ll);
                            }

                            if(data.getElapsedTimestamps()[i] == mTrial.getPauseTimes().get(j)[1] ||
                                    (i < data.getElapsedTimestamps().length - 1 && data.getElapsedTimestamps()[i] < mTrial.getPauseTimes().get(j)[1] && data.getElapsedTimestamps()[i+1] > mTrial.getPauseTimes().get(j)[1] )) {

                                LimitLine ll2 = new LimitLine(i);
                                ll2.setLineColor(Color.YELLOW);
                                ll2.setLineWidth(2f);
                                xAxis.addLimitLine(ll2);
                            }
                        }
                    }

                }
            } else {
                CardView card = (CardView)findViewById(R.id.card_accel_graph);
                card.setVisibility(View.GONE);
            }

            LineData graphData2 = mStepTimeGraph.getData();

            int[] steps = mTrial.getStepTimes();

            for(int i=0; i<steps.length-1; i++) {

                // Try to avoid plotting pauses by checking if the difference between step times is large
                if (steps[i+1] - steps[i] < mTrial.getMeanStepTime() * 1.5) {
                    graphData2.addXValue(i + 1 + "");
                    graphData2.addEntry(new Entry((steps[i+1] - steps[i]), i), 0);
                }


            }

            mAccelGraph.notifyDataSetChanged();
            mAccelGraph.invalidate();

            mStepTimeGraph.notifyDataSetChanged();
            mStepTimeGraph.invalidate();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Click listener for values in the graph
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (dataSetIndex == 1) {
            final int[] stepsOriginal = mTrial.getStepTimes();

            // Get data point from graph
            int data_selected = mTrial.getTrialData().getElapsedTimestamps()[e.getXIndex()];

            // Convert int[] to Integer[] so we can look up element by value
            Integer[] stepTimesInteger = new Integer[mTrial.getStepTimes().length];
            int i = 0;
            for (int value : stepsOriginal) {
                stepTimesInteger[i++] = Integer.valueOf(value);
            }

            // Find index of the step time from Integer version of stepsOriginal (stepTimesInteger)
            final ArrayList<Integer> stepTimesIntegerList = new ArrayList<Integer>(Arrays.asList(stepTimesInteger));
            final int index_to_remove = Arrays.asList(stepTimesInteger).indexOf(data_selected);
            Log.d(TAG, "data index: " + index_to_remove);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.remove_step)
                    .setMessage("Step Time: " + data_selected + "ms, index: " + index_to_remove );

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    stepTimesIntegerList.remove(index_to_remove);

                    int[] new_step_times = new int[stepsOriginal.length - 1];
                    for (int j = 0; j < stepsOriginal.length - 1; j++) {
                        new_step_times[j] = stepTimesIntegerList.get(j);
                    }

                    updateStepTimes(new_step_times);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            builder.show();

        }


    }

    public void updateStepTimes(int[] new_step_times) {

        // Update the step times and recalculate metrics
        LocalDatabaseAccess.addTrialSteps(this, mTrial.getTrialId(), new_step_times, mTrial.getPauseTimes());

        mTrial.setStepTimes(new_step_times);

        float mean_step = Gait.getMeanStepTime(new_step_times, mTrial.getPauseTimes());
        float step_sd = Gait.getStepSD(new_step_times, mean_step, mTrial.getPauseTimes());
        float step_cv = Gait.getStepCV(step_sd, mean_step);
        float sym = Gait.getGaitSymmetry(new_step_times, mTrial.getPauseTimes());
        float mean_stride = Gait.getMeanStrideTime(new_step_times, mTrial.getPauseTimes());
        float cadence = mTrial.getCadence();
        float stride_sd = Gait.getStrideSD(new_step_times, mean_stride, mTrial.getPauseTimes());
        float stride_cv = Gait.getStrideCV(stride_sd, mean_stride);
        mTrial.setStepAnalysis(mean_step, step_sd, step_cv, sym, cadence, mean_stride, stride_sd, stride_cv);

        LocalDatabaseAccess.updateTrial(this, mTrial);

        UpdateTask task = new UpdateTask(mTrial, mUsername, this);
        task.execute();

        finish();
        startActivity(getIntent());
    }

    private class UpdateTask extends AsyncTask<Void, Void, Boolean> {

        private Trial trial;
        private String username;
        private Context context;

        public UpdateTask(Trial the_trial, String user, Context ctx) {
            trial = the_trial;
            username = user;
            context = ctx;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            return DataService.updateTrial(context, trial, username);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            updateComplete(success);
            super.onPostExecute(success);
        }
    }

    public void updateComplete(boolean success) {
        if(success) {
            Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else {
            Toast.makeText(this, "Failed to update assessment, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu with delete icon.
        getMenuInflater().inflate(R.menu.menu_assessment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle menu item clicks, only one option in this case
        if (id == R.id.delete) {
            DeleteTask task = new DeleteTask(mTrial.getTrialId(), mUsername, this);
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteComplete(boolean success) {
        if(success) {
            Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else {
            Toast.makeText(this, "Failed to delete assessment, try again.", Toast.LENGTH_SHORT).show();
        }
    }



    private class DeleteTask extends AsyncTask<Void, Void, Boolean> {
        private int trialId;
        private String username;
        private Context context;

        public DeleteTask(int tId, String user, Context ctx) {
            trialId = tId;
            username = user;
            context = ctx;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return DataService.deleteTrial(context, trialId, username);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            deleteComplete(success);
            super.onPostExecute(success);
        }
    }
}
